package com.bsds.ddf.server.paxos;

import com.bsds.ddf.server.ServerLogger;

import org.springframework.beans.factory.annotation.Autowired;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class implementing the functions mentioned in the Learner interface.
 */
public class LearnerImpl implements Learner {

  @Autowired
  private Acceptor acceptor;

  @Autowired
  private Proposer proposer;

  class Proposal {
    String key;
    int acceptCount;
    int retentionCount;
    String value;

    Proposal(String key, int acceptCount, int retentionCount, String value) {
      this.key = key;
      this.acceptCount = acceptCount;
      this.retentionCount = retentionCount;
      this.value = value;
    }
  }

  private final Messenger messenger;
  private final int quorumSize;

  private Map<String, Map<ProposalID, Proposal>> proposalsMap = new ConcurrentHashMap<>();
  private Map<String, Map<String, ProposalID>> acceptorsMap = new ConcurrentHashMap<>();
  private Map<String, String> finalValueMap = new ConcurrentHashMap<>();
  private Map<String, ProposalID> finalProposalIDMap = new ConcurrentHashMap<>();

  private static Learner instance;

  public static Learner getInstance(Messenger messenger, int quorumSize) {
    if (instance == null) {
      instance = new LearnerImpl(messenger, quorumSize);
    }
    return instance;
  }

  private LearnerImpl(Messenger messenger, int quorumSize) {
    this.messenger = messenger;
    this.quorumSize = quorumSize;
  }

  @Override
  public boolean isComplete(String UUID) {
    if (RequestHandler.activeRequests.containsKey(UUID)) {
      return RequestHandler.activeRequests.get(UUID).equals("Completed");
    }
    return false;
  }

  @Override
  public void receiveAccepted(String key, String fromUID, ProposalID proposalID,
                              String acceptedValue, String requestUUID) throws NotBoundException, RemoteException {
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
      String finalValue = finalValueMap.getOrDefault(key, null);

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
  public void commit(String key, String value, String requestUUID) throws RemoteException {
    Runnable runnable = () -> {
      String message = String.format("Learner.commit(key=%s, value=%s)"
              , key, value);
      ServerLogger.log(message);
//      if (!value.equals(""))
//        KeyValue.put(key, value);
//      else
//        KeyValue.delete(key);

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
  public void resetState(String key) {
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
}
