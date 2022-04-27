package com.bsds.ddf.server.paxos;

import com.bsds.ddf.server.ServerLogger;
import com.bsds.ddf.server.entities.UserFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;


@Component
public class ProposerImpl implements Proposer {
  protected Messenger messenger;
  protected String proposerUID;
  protected int quorumSize;

  protected Map<RequestKey, ProposalID> proposalIDMap = new ConcurrentHashMap<>();
  protected Map<RequestKey, UserFile> proposedValueMap = new ConcurrentHashMap<>();
  protected Map<RequestKey, ProposalID> lastAcceptedIDMap = new ConcurrentHashMap<>();
  protected Map<RequestKey, Set<String>> promisesReceivedMap = new ConcurrentHashMap<>();

  @Autowired
  private ProposerImpl(Messenger messenger, @Qualifier("serverPort") Integer proposerUID,
                       @Qualifier("allServers") AllServers allServers) {
    this.messenger = messenger;
    this.proposerUID = String.valueOf(proposerUID);
    this.quorumSize = ((allServers.getAllPorts().size() + 1) / 2) + 1;
  }

  @Override
  public void setProposal(RequestKey key, UserFile value) {
    if (!proposedValueMap.containsKey(key) || (proposedValueMap.containsKey(key) && !proposedValueMap.get(key).equals(value))) {
      proposedValueMap.put(key, value);
    }
  }

  @Override
  public void prepare(RequestKey key, String requestUUID) throws NotBoundException, RemoteException {
    Runnable runnable = () -> {
      String message = String.format("Proposer.prepare(key=%s)", key);
      ServerLogger.log(message);

      Set<String> promisesReceived = promisesReceivedMap.getOrDefault(key, new HashSet<>());
      promisesReceived.clear();
      promisesReceivedMap.put(key, promisesReceived);

      ProposalID proposalID = proposalIDMap.getOrDefault(key, new ProposalID(0, proposerUID));
      proposalID.incrementNumber();
      proposalIDMap.put(key, proposalID);

      try {
        messenger.sendPrepare(key, proposalID, requestUUID);
      } catch (RemoteException | NotBoundException e) {
        message = String.format("Proposer.prepare exception occurred: %s", e.getMessage());
        ServerLogger.log(message);
      }
    };

    runnable.run();
  }

  @Override
  public void receivePromise(RequestKey key, String fromUID, ProposalID proposalID, ProposalID prevAcceptedID, UserFile prevAcceptedValue, String requestUUID) throws NotBoundException, RemoteException {
    Runnable runnable = () -> {
      String message = String.format("Proposer.receivePromise(key=%s, fromUID=%s, proposalID=%s, prevAcceptedID=%s, prevAcceptedValue=%s)", key, fromUID, proposalID.toString(), prevAcceptedID, prevAcceptedValue);
      ServerLogger.log(message);

      ProposalID currProposalID = proposalIDMap.getOrDefault(key, new ProposalID(0, proposerUID));
      Set<String> promisesReceived = promisesReceivedMap.getOrDefault(key, new HashSet<>());
      UserFile proposedValue = proposedValueMap.getOrDefault(key, null);

      if (!proposalID.equals(currProposalID) || promisesReceived.contains(fromUID)) return;

      promisesReceived.add(fromUID);

      ProposalID lastAcceptedID = lastAcceptedIDMap.getOrDefault(key, null);
      if (lastAcceptedID == null || (prevAcceptedID != null && prevAcceptedID.isGreaterThan(lastAcceptedID))) {
        lastAcceptedID = prevAcceptedID;

        if (prevAcceptedValue != null) proposedValue = prevAcceptedValue;
      }

      if (promisesReceived.size() == quorumSize) if (proposedValue != null) {
        try {
          messenger.sendAccept(key, currProposalID, proposedValue, requestUUID);
        } catch (RemoteException | NotBoundException e) {
          message = String.format("Proposer.receivePromise exception occurred: %s", e.getMessage());
          ServerLogger.log(message);
        }
      }

      promisesReceivedMap.put(key, promisesReceived);
      if (lastAcceptedID != null) lastAcceptedIDMap.put(key, lastAcceptedID);
      if (proposedValue != null) proposedValueMap.put(key, proposedValue);
    };
    runnable.run();
  }

  @Override
  public void resetState(RequestKey key) {
    Runnable runnable = () -> {
      proposalIDMap.remove(key);
      proposedValueMap.remove(key);
      lastAcceptedIDMap.remove(key);
      promisesReceivedMap.remove(key);
    };
    runnable.run();
  }
}
