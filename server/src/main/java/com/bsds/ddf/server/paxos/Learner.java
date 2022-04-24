package com.bsds.ddf.server.paxos;


import com.bsds.ddf.server.entities.UserFile;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for the learner role of the server
 */
public interface Learner extends Remote {
	/**
	 * Method checks if the request with the given key has been completed or not.
	 * @param key key to check for
	 * @return boolean representing if request is completed.
	 * @throws RemoteException
	 */
	boolean isComplete(String UUID) throws RemoteException;

	/**
	 * Method called to indicate that a particular server has accepted a value
	 * @param key key for the command
	 * @param fromUID ID of the server that send the prepare message
	 * @param proposalID Proposal ID of the message
	 * @param acceptedValue the accepted value
	 * @param requestUUID ID of the request
	 * @throws NotBoundException
	 * @throws RemoteException
	 */
	void receiveAccepted(RequestKey key, String fromUID, ProposalID proposalID,
											 UserFile acceptedValue, String requestUUID) throws NotBoundException, RemoteException;

	/**
	 * Method to call when a key value pair has to be committed.
	 */
	void commit(RequestKey key, UserFile value, String requestUUID) throws RemoteException;

	/**
	 * Method to reset the state of the learner once the value has been committed. This method also
	 * resets the state of both proposer and acceptor.
	 */
	void resetState(RequestKey key) throws RemoteException;
}
