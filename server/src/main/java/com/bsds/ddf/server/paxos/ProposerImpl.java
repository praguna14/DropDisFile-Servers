package com.bsds.ddf.server.paxos;

import com.bsds.ddf.server.ServerLogger;

import org.springframework.stereotype.Component;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class ProposerImpl implements Proposer {
  protected Messenger messenger;
  protected String proposerUID;
  protected final int quorumSize;

  protected Map<String, ProposalID> proposalIDMap = new ConcurrentHashMap<>();
  protected Map<String, String> proposedValueMap = new ConcurrentHashMap<>();
  protected Map<String, ProposalID> lastAcceptedIDMap = new ConcurrentHashMap<>();
  protected Map<String, Set<String>> promisesReceivedMap = new ConcurrentHashMap<>();

  private static Proposer instance;

  public static Proposer getInstance(Messenger messenger, String proposerUID, int quorumSize) {
    if (instance == null) {
      instance = new ProposerImpl(messenger, proposerUID, quorumSize);
    }
    return instance;
  }

  private ProposerImpl(Messenger messenger, String proposerUID, int quorumSize) {
    this.messenger = messenger;
    this.proposerUID = proposerUID;
    this.quorumSize = quorumSize;
  }

  @Override
  public void setProposal(String key, String value) {
    if (!proposedValueMap.containsKey(key) || (proposedValueMap.containsKey(key) && !proposedValueMap.get(key).equals(value))) {
      proposedValueMap.put(key, value);
    }
  }

  @Override
  public void prepare(String key, String requestUUID) throws NotBoundException, RemoteException {
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
  public void receivePromise(String key, String fromUID, ProposalID proposalID, ProposalID prevAcceptedID, String prevAcceptedValue, String requestUUID) throws NotBoundException, RemoteException {
    Runnable runnable = () -> {
      String message = String.format("Proposer.receivePromise(key=%s, fromUID=%s, proposalID=%s, prevAcceptedID=%s, prevAcceptedValue=%s)", key, fromUID, proposalID.toString(), prevAcceptedID, prevAcceptedValue);
      ServerLogger.log(message);

      ProposalID currProposalID = proposalIDMap.getOrDefault(key, new ProposalID(0, proposerUID));
      Set<String> promisesReceived = promisesReceivedMap.getOrDefault(key, new HashSet<>());
      String proposedValue = proposedValueMap.getOrDefault(key, null);

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
  public void resetState(String key) {
    Runnable runnable = () -> {
      proposalIDMap.remove(key);
      proposedValueMap.remove(key);
      lastAcceptedIDMap.remove(key);
      promisesReceivedMap.remove(key);
    };
    runnable.run();
  }
}
