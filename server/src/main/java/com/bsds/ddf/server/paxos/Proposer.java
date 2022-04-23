package com.bsds.ddf.server.paxos;


import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Implements all the functionalities of a proposer
 */
public interface Proposer extends Remote {

	public void setProposal(String key, String value) throws RemoteException;

	public void prepare(String key, String requestUUID) throws NotBoundException, RemoteException;

	public void receivePromise(String key, String fromUID, ProposalID proposalID,
											ProposalID prevAcceptedID, String prevAcceptedValue, String requestUUID) throws NotBoundException, RemoteException;

	public void resetState(String key) throws RemoteException;
}
