package com.bsds.ddf.server.paxos;

import com.bsds.ddf.server.ServerLogger;
import com.bsds.ddf.server.entities.UserFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class implementing the functionalities mentioned in the Acceptor interface
 */
@Component
public class AcceptorImpl implements Acceptor {

  @Autowired
  protected Messenger messenger;

  protected Map<RequestKey, ProposalID> promisedIDMap = new ConcurrentHashMap<>();
  protected Map<RequestKey, ProposalID> acceptedIDMap = new ConcurrentHashMap<>();
  protected Map<RequestKey, UserFile> acceptedValueMap = new ConcurrentHashMap<>();

  private static Acceptor instance;


  /**
   * Method to receive a prepare message from a proposer
   *
   * @param key         key for the command
   * @param fromUID     ID of the server that send the prepare message
   * @param proposalID  Proposal ID of the message
   * @param requestUUID ID to identify the request
   * @throws NotBoundException
   * @throws RemoteException
   */
  @Override
  public void receivePrepare(RequestKey key, String fromUID, ProposalID proposalID, String requestUUID) throws NotBoundException, RemoteException {
    Runnable runnable = () -> {
      String message = String.format("Acceptor.receivePrepare(key=%s, fromUID=%s, proposalID=%s)", key, fromUID, proposalID.toString());
      ServerLogger.log(message);

      ProposalID promisedID = promisedIDMap.getOrDefault(key, null);
      ProposalID acceptedID = acceptedIDMap.getOrDefault(key, null);
      UserFile acceptedValue = null;

      try {
        if (promisedID != null && proposalID.equals(promisedID)) { // duplicate message
          messenger.sendPromise(key, fromUID, proposalID, acceptedID, acceptedValue, requestUUID);
        } else if (promisedID == null || proposalID.isGreaterThan(promisedID)) {
          promisedID = proposalID;
          messenger.sendPromise(key, fromUID, proposalID, acceptedID, acceptedValue, requestUUID);
          promisedIDMap.put(key, promisedID);
        }
      } catch (NotBoundException | RemoteException e) {
        message = String.format("Acceptor.receivePrepare exception occurred: %s", e.getMessage());
        ServerLogger.log(message);
      }
    };
    runnable.run();
  }

  /**
   * Method to receive the accept request after quorum of promise has been met by one of the server
   *
   * @param key         key for the command
   * @param fromUID     ID of the server that send the prepare message
   * @param proposalID  Proposal ID of the message
   * @param value       value to be set in the server
   * @param requestUUID ID to identify the request
   * @throws NotBoundException
   * @throws RemoteException
   */
  @Override
  public void receiveAcceptRequest(RequestKey key, String fromUID, ProposalID proposalID,
                                   UserFile value, String requestUUID) throws NotBoundException, RemoteException {
    Runnable runnable = () -> {
      String message = String.format("Acceptor.receiveAcceptRequest(key=%s, fromUID=%s, proposalID=%s, value=%s)"
              , key, fromUID, proposalID.toString(), value, requestUUID);
      ServerLogger.log(message);

      ProposalID promisedID = promisedIDMap.getOrDefault(key, null);
      ProposalID acceptedID = acceptedIDMap.getOrDefault(key, null);
      UserFile acceptedValue = acceptedValueMap.getOrDefault(key, null);

      if (promisedID == null || proposalID.isGreaterThan(promisedID) || proposalID.equals(promisedID)) {
        promisedID = proposalID;
        acceptedID = proposalID;
        acceptedValue = value;

        try {
          messenger.sendAccepted(key, acceptedID, acceptedValue, requestUUID);
        } catch (RemoteException | NotBoundException e) {
          message = String.format("Acceptor.receiveAcceptRequest exception occurred: %s", e.getMessage());
          ServerLogger.log(message);
        }
        promisedIDMap.put(key, promisedID);
        acceptedIDMap.put(key, acceptedID);
        acceptedValueMap.put(key, acceptedValue);
      }
    };
    runnable.run();
  }

  /**
   * Method to reset the state of the acceptor once a value has been saved.
   *
   * @param key
   */
  @Override
  public void resetState(RequestKey key) {
    promisedIDMap.remove(key);
    acceptedIDMap.remove(key);
    acceptedValueMap.remove(key);
  }
}
