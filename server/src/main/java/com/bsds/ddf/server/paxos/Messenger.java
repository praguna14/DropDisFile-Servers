package com.bsds.ddf.server.paxos;

import com.bsds.ddf.server.entities.UserFile;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Represents all the functionalities of a messenger
 */
public interface Messenger extends Remote {

  void sendPrepare(RequestKey key, ProposalID proposalID, String requestUUID) throws NotBoundException, RemoteException;

  void sendPromise(RequestKey key, String proposerUID, ProposalID proposalID, ProposalID previousID,
                   UserFile acceptedValue, String requestUUID) throws NotBoundException, RemoteException;

  void sendAccept(RequestKey key, ProposalID proposalID, UserFile proposalValue, String requestUUID) throws NotBoundException, RemoteException;

  void sendAccepted(RequestKey key, ProposalID proposalID, UserFile acceptedValue, String requestUUID) throws NotBoundException, RemoteException;

  void onResolution(RequestKey key, ProposalID proposalID, UserFile value, String requestUUID) throws NotBoundException, RemoteException;
}
	