package com.bsds.ddf.server.paxos;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Represents all the functionalities of a messenger
 */
public interface Messenger extends Remote {

  void sendPrepare(String key, ProposalID proposalID, String requestUUID) throws NotBoundException, RemoteException;

  void sendPromise(String key, String proposerUID, ProposalID proposalID, ProposalID previousID,
                   String acceptedValue, String requestUUID) throws NotBoundException, RemoteException;

  void sendAccept(String key, ProposalID proposalID, String proposalValue, String requestUUID) throws NotBoundException, RemoteException;

  void sendAccepted(String key, ProposalID proposalID, String acceptedValue, String requestUUID) throws NotBoundException, RemoteException;

  void onResolution(String key, ProposalID proposalID, String value, String requestUUID) throws NotBoundException, RemoteException;
}
	