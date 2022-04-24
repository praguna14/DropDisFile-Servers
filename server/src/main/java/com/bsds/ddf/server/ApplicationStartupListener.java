package com.bsds.ddf.server;

import com.bsds.ddf.server.paxos.Acceptor;
import com.bsds.ddf.server.paxos.Learner;
import com.bsds.ddf.server.paxos.Proposer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

@Component
public class ApplicationStartupListener implements
        ApplicationListener<ContextRefreshedEvent> {

  @Autowired
  Proposer proposer;

  @Autowired
  Acceptor acceptor;

  @Autowired
  Learner learner;

  @Value("${server.port}")
  private String webServerPort;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
// Object to handle client requests.
    try {
      Proposer proposerStub = (Proposer) UnicastRemoteObject.exportObject(proposer, 0);
      ServerLogger.log("Proposer Stub object created");
      Acceptor acceptorStub = (Acceptor) UnicastRemoteObject.exportObject(acceptor, 0);
      ServerLogger.log("Acceptor Stub object created");
      Learner learnerStub = (Learner) UnicastRemoteObject.exportObject(learner, 0);
      ServerLogger.log("Learner Stub object created");

      // create registry on the port provided
      int port = Integer.parseInt(webServerPort);
      Registry registry = LocateRegistry.createRegistry(port);
      ServerLogger.log("Registry object created");

      registry.rebind("proposer", proposerStub);
      ServerLogger.log("proposer Object bound to registry");
      registry.rebind("acceptor", acceptorStub);
      ServerLogger.log("acceptor Object bound to registry");
      registry.rebind("learner", learnerStub);
      ServerLogger.log("learner Object bound to registry");
    } catch (RemoteException e) {
      ServerLogger.log(String.format("Start Server failed due to : %s", e.getMessage()));
    }
  }
}