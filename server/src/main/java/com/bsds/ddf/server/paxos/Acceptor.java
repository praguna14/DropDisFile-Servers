package com.bsds.ddf.server.paxos;


import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface representing all the functionalities of an acceptor.
 */
public interface Acceptor extends Remote {

	void receivePrepare(String key, String fromUID, ProposalID proposalID, String requestUUID) throws NotBoundException, RemoteException;

	void receiveAcceptRequest(String key, String fromUID, ProposalID proposalID,
														String value, String requestUUID) throws NotBoundException, RemoteException;

	void resetState(String key) throws RemoteException;
}
