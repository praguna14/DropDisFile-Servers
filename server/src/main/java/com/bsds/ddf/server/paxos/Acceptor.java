package com.bsds.ddf.server.paxos;


import com.bsds.ddf.server.entities.UserFile;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface representing all the functionalities of an acceptor.
 */
public interface Acceptor extends Remote {

	void receivePrepare(RequestKey key, String fromUID, ProposalID proposalID, String requestUUID) throws NotBoundException, RemoteException;

	void receiveAcceptRequest(RequestKey key, String fromUID, ProposalID proposalID,
														UserFile value, String requestUUID) throws NotBoundException, RemoteException;

	void resetState(RequestKey key) throws RemoteException;
}
