package com.bsds.ddf.server;

import com.bsds.ddf.server.entities.UserFile;
import com.bsds.ddf.server.paxos.Acceptor;
import com.bsds.ddf.server.paxos.Learner;
import com.bsds.ddf.server.paxos.Proposer;
import com.bsds.ddf.server.service.FileService;
import com.bsds.ddf.server.service.RestService;

import org.apache.catalina.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

@Component
public class ApplicationStartupListener implements
        ApplicationListener<ContextRefreshedEvent> {

  @Autowired
  @Qualifier("serverPort")
  private Integer webServerPort;

  @Autowired
  private RestService restService;

  @Autowired
  private FileService fileService;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    ServerLogger.init(String.valueOf(webServerPort));
//    initializeDatabase();
  }

//  private void initializeDatabase(){
//    ServerLogger.log("Initializing Database.");
//    try {
//      List<Integer> servers = restService.getServers();
//      if(servers.size() > 0){
//        ServerLogger.log(String.format("Servers found: %s", servers.toString()));
//        int serverPort = servers.get(0);
//        ServerLogger.log(String.format("Server Port selected: %d", serverPort));
//
//        List<UserFile> userFiles = restService.fetchAllFiles(serverPort);
//        ServerLogger.log(String.format("Total files received: %d", userFiles.size()));
//
//        for(UserFile userFile:userFiles){
//          fileService.addUserFile(userFile);
//          ServerLogger.log(String.format("Saved file:%s", userFile.getFilename()));
//        }
//        ServerLogger.log(String.format("All files saved"));
//      } else{
//        ServerLogger.log("No servers returned by CMS");
//      }
//    } catch(HttpStatusCodeException ex){
//      ServerLogger.log("Error while fetching servers");
//    }
//  }
}