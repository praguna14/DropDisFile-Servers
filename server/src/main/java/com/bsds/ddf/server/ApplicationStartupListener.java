package com.bsds.ddf.server;

import com.bsds.ddf.server.paxos.Acceptor;
import com.bsds.ddf.server.paxos.Learner;
import com.bsds.ddf.server.paxos.Proposer;

import org.apache.catalina.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
  @Qualifier("serverPort")
  private Integer webServerPort;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
// Object to handle client requests.

    ServerLogger.init(String.valueOf(webServerPort));
  }
}