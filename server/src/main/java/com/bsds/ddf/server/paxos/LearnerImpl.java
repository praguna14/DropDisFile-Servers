package com.bsds.ddf.server.paxos;

import com.bsds.ddf.server.ServerLogger;
import com.bsds.ddf.server.entities.UserFile;
import com.bsds.ddf.server.service.FileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

/**
 * Class implementing the functions mentioned in the Learner interface.
 */
@Component
public class LearnerImpl implements Learner {

  private Acceptor acceptor;
  private Proposer proposer;
  private FileService fileService;
  private Messenger messenger;
  private int quorumSize;

  @Autowired
  public LearnerImpl(Acceptor acceptor, Proposer proposer, FileService fileService, Messenger messenger, @Qualifier("allPorts") List<Integer> allPorts){
    this.acceptor = acceptor;
    this.proposer = proposer;
    this.fileService = fileService;
    this.messenger = messenger;
    this.quorumSize = ((allPorts.size() + 1) / 2) + 1;
  }

  class Proposal {
    RequestKey key;
    int acceptCount;
    int retentionCount;
    UserFile value;

    Proposal(RequestKey key, int acceptCount, int retentionCount, UserFile value) {
      this.key = key;
      this.acceptCount = acceptCount;
      this.retentionCount = retentionCount;
      this.value = value;
    }
  }

  private Map<RequestKey, Map<ProposalID, Proposal>> proposalsMap = new ConcurrentHashMap<>();
  private Map<RequestKey, Map<String, ProposalID>> acceptorsMap = new ConcurrentHashMap<>();
  private Map<RequestKey, UserFile> finalValueMap = new ConcurrentHashMap<>();
  private Map<RequestKey, ProposalID> finalProposalIDMap = new ConcurrentHashMap<>();


  @Override
  public boolean isComplete(String UUID) {
    if (RequestHandler.activeRequests.containsKey(UUID)) {
      return RequestHandler.activeRequests.get(UUID).equals("Completed");
    }
    return false;
  }

  @Override
  public void receiveAccepted(RequestKey key, String fromUID, ProposalID proposalID,
                              UserFile acceptedValue, String requestUUID) throws NotBoundException, RemoteException {
    Runnable runnable = () -> {
      if (RequestHandler.activeRequests.containsKey(requestUUID)
              && RequestHandler.activeRequests.get(requestUUID).equals("Completed")) {
        return;
      }
      String message = String.format("Learner.receiveAccepted(key=%s, fromUID=%s, proposalID=%s, value=%s)"
              , key, fromUID, proposalID.toString(), acceptedValue);
      ServerLogger.log(message);

      Map<String, ProposalID> acceptors = acceptorsMap.getOrDefault(key, new ConcurrentHashMap<>());
      Map<ProposalID, Proposal> proposals = proposalsMap.getOrDefault(key, new ConcurrentHashMap<>());
      ProposalID finalProposalID = finalProposalIDMap.getOrDefault(key, null);
      UserFile finalValue = finalValueMap.getOrDefault(key, null);

      if (isComplete(requestUUID))
        return;

      ProposalID oldPID = acceptors.get(fromUID);

      if (oldPID != null && !proposalID.isGreaterThan(oldPID))
        return;

      acceptors.put(fromUID, proposalID);

      if (oldPID != null) {
        Proposal oldProposal = proposals.get(oldPID);
        oldProposal.retentionCount -= 1;
        if (oldProposal.retentionCount == 0)
          proposals.remove(oldPID);
      }

      if (!proposals.containsKey(proposalID))
        proposals.put(proposalID, new Proposal(key, 0, 0, acceptedValue));

      Proposal thisProposal = proposals.get(proposalID);

      thisProposal.acceptCount += 1;
      thisProposal.retentionCount += 1;

      if (thisProposal.acceptCount == quorumSize) {
        finalProposalID = proposalID;
        finalValue = acceptedValue;
        proposals.clear();
        acceptors.clear();

        try {
          messenger.onResolution(key, proposalID, acceptedValue, requestUUID);
        } catch (RemoteException | NotBoundException e) {
          message = String.format("Learner.receiveAccepted exception occurred: %s", e.getMessage());
          ServerLogger.log(message);
        }
      }

      acceptorsMap.put(key, acceptors);
      proposalsMap.put(key, proposals);
      if (finalProposalID != null)
        finalProposalIDMap.put(key, finalProposalID);
      if (finalValue != null)
        finalValueMap.put(key, finalValue);
    };
    runnable.run();
  }

  @Override
  public void commit(RequestKey key, UserFile value, String requestUUID) throws RemoteException {
    Runnable runnable = () -> {
      String message = String.format("Learner.commit(key=%s, value=%s)"
              , key, value);
      ServerLogger.log(message);
      if (!isEmptyFile(value))
        saveFile(value);
      else
        deleteFile(key.getUsername(), key.getFilename());

      updateRequestHandlerState(requestUUID);

      try {
        proposer.resetState(key);
        acceptor.resetState(key);
      } catch (RemoteException e) {
        message = String.format("Learner.commit exception occurred: %s", e.getMessage());
        ServerLogger.log(message);
      }

      resetState(key);
    };
    runnable.run();
  }

  @Override
  public void resetState(RequestKey key) {
    Runnable runnable = () -> {
      acceptorsMap.remove(key);
      proposalsMap.remove(key);
      finalProposalIDMap.remove(key);
      finalValueMap.remove(key);
    };
    runnable.run();
  }

  private void updateRequestHandlerState(String requestUUID) {
    RequestHandler.activeRequests.put(requestUUID, "Completed");
  }

  private void saveFile(UserFile file) {
    fileService.addUserFile(file);
  }

  private void deleteFile(String userName, String fileName) {
    fileService.deleteFileForUser(userName, fileName);
  }

  private boolean isEmptyFile(UserFile file){
    return file.getContent() == null || file.getContent().equals("");
  }
}
