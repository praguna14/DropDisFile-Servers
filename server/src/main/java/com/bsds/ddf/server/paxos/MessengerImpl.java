package com.bsds.ddf.server.paxos;

import com.bsds.ddf.server.entities.UserFile;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

@Component
public class MessengerImpl implements Messenger {
  private String webServerPort;
  private List<Integer> allPorts;

  private int quorumSize;

  public MessengerImpl(AllServers allServers, @Qualifier("rmiPort") int webServerPort) {
    this.allPorts = allServers.getAllPorts();
    this.webServerPort = String.valueOf(webServerPort);
    quorumSize = ((allPorts.size() + 1) / 2) + 1;
  }

  @Override
  public void sendPrepare(RequestKey key, ProposalID proposalID, String requestUUID) throws NotBoundException, RemoteException {
    Acceptor acceptor = getAcceptor(Integer.parseInt(webServerPort));
    acceptor.receivePrepare(key, webServerPort, proposalID, requestUUID);

    for (Integer port : allPorts) {
      getAcceptor(port).receivePrepare(key, webServerPort, proposalID, requestUUID);
    }
  }

  @Override
  public void sendPromise(RequestKey key, String proposerUID, ProposalID proposalID, ProposalID previousID,
                          UserFile acceptedValue, String requestUUID) throws NotBoundException, RemoteException {
    Proposer proposer = getProposer(Integer.parseInt(proposerUID));
    proposer.receivePromise(key, webServerPort, proposalID, previousID, acceptedValue, requestUUID);
  }

  @Override
  public void sendAccept(RequestKey key, ProposalID proposalID, UserFile proposalValue, String requestUUID) throws NotBoundException, RemoteException {
    Acceptor acceptor = getAcceptor(Integer.parseInt(webServerPort));
    acceptor.receivePrepare(key, webServerPort, proposalID, requestUUID);

    for (Integer port : allPorts) {
      getAcceptor(port).receiveAcceptRequest(key, webServerPort, proposalID, proposalValue, requestUUID);
    }
  }

  @Override
  public void sendAccepted(RequestKey key, ProposalID proposalID, UserFile acceptedValue, String requestUUID) throws NotBoundException, RemoteException {
    Learner learner = getLearner(Integer.parseInt(webServerPort));
    learner.receiveAccepted(key, webServerPort, proposalID, acceptedValue, requestUUID);

    for (Integer port : allPorts) {
      getLearner(port).receiveAccepted(key, webServerPort, proposalID, acceptedValue, requestUUID);
    }
  }

  @Override
  public void onResolution(RequestKey key, ProposalID proposalID, UserFile value, String requestUUID) throws NotBoundException, RemoteException {
    Learner learner = getLearner(Integer.parseInt(webServerPort));
    learner.commit(key, value, requestUUID);

    for (Integer port : allPorts) {
      getLearner(port).commit(key, value, requestUUID);
    }
  }

  private Proposer getProposer(Integer port) throws NotBoundException, RemoteException {
    int serverPort = allPorts.stream()
            .filter(uid -> uid.equals(port))
            .findFirst().get();

    Registry registry = LocateRegistry.getRegistry(serverPort);
    // Lookup the remote object on the host
    return (Proposer) registry.lookup("proposer");
  }

  private Acceptor getAcceptor(Integer port) throws NotBoundException, RemoteException {
    int serverPort = allPorts.stream()
            .filter(uid -> uid.equals(port))
            .findFirst().get();

    Registry registry = LocateRegistry.getRegistry(serverPort);
    // Lookup the remote object on the host
    return (Acceptor) registry.lookup("acceptor");
  }

  private Learner getLearner(Integer port) throws RemoteException, NotBoundException {
    int serverPort = allPorts.stream()
            .filter(uid -> uid.equals(port))
            .findFirst().get();

    Registry registry = LocateRegistry.getRegistry(serverPort);
    // Lookup the remote object on the host
    return (Learner) registry.lookup("learner");
  }
}
