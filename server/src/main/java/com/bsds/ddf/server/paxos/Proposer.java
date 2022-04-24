package com.bsds.ddf.server.paxos;


import com.bsds.ddf.server.entities.UserFile;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Implements all the functionalities of a proposer
 */
public interface Proposer extends Remote {

	public void setProposal(RequestKey key, UserFile value) throws RemoteException;

	public void prepare(RequestKey key, String requestUUID) throws NotBoundException, RemoteException;

	public void receivePromise(RequestKey key, String fromUID, ProposalID proposalID,
											ProposalID prevAcceptedID, UserFile prevAcceptedValue, String requestUUID) throws NotBoundException, RemoteException;

	public void resetState(RequestKey key) throws RemoteException;
}
