package com.bsds.ddf.server.paxos;

import com.bsds.ddf.server.ServerLogger;
import com.bsds.ddf.server.entities.UserFile;
import com.bsds.ddf.server.pojo.AcceptRequestPojo;
import com.bsds.ddf.server.pojo.AcceptedRequestPojo;
import com.bsds.ddf.server.pojo.CommitRequestPojo;
import com.bsds.ddf.server.pojo.PreparePojo;
import com.bsds.ddf.server.pojo.PromisePOJO;
import com.bsds.ddf.server.service.RestService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

@Component
public class MessengerImpl implements Messenger {
  private String webServerPort;
  private AllServers allServers;
  private RestService restService;

  private int quorumSize;

  public MessengerImpl(AllServers allServers, @Qualifier("serverPort") int webServerPort, RestService restService) {
    this.allServers = allServers;
    this.restService = restService;
    List<Integer> allPorts = allServers.getAllPorts();
    this.webServerPort = String.valueOf(webServerPort);
    quorumSize = ((allPorts.size()) / 2) + 1;
  }

  @Override
  public void sendPrepare(RequestKey key, ProposalID proposalID, String requestUUID) throws NotBoundException, RemoteException {
    PreparePojo request = PreparePojo.builder()
            .key(key)
            .fromUID(webServerPort)
            .proposalID(proposalID)
            .requestUUID(requestUUID)
            .build();
    List<Integer> allPorts = allServers.getAllPorts();
    for (Integer port : allPorts) {
      ServerLogger.log(String.format("Sending sendPrepare to %d", port));
      restService.sendPrepare(request, port);
    }
  }

  @Override
  public void sendPromise(RequestKey key, String proposerUID, ProposalID proposalID, ProposalID previousID,
                          UserFile acceptedValue, String requestUUID) throws NotBoundException, RemoteException {
    ServerLogger.log(String.format("Sending sendAccepted to %s", proposerUID));
    PromisePOJO request = PromisePOJO.builder()
            .key(key)
            .fromUID(webServerPort)
            .proposalID(proposalID)
            .prevAcceptedID(previousID)
            .prevAcceptedValue(acceptedValue)
            .requestUUID(requestUUID)
            .build();
    restService.sendPromise(request, Integer.parseInt(proposerUID));
  }

  @Override
  public void sendAccept(RequestKey key, ProposalID proposalID, UserFile proposalValue, String requestUUID) throws NotBoundException, RemoteException {
    List<Integer> allPorts = allServers.getAllPorts();
    AcceptRequestPojo request = AcceptRequestPojo.builder()
            .key(key)
            .fromUID(webServerPort)
            .proposalID(proposalID)
            .value(proposalValue)
            .requestUUID(requestUUID)
            .build();
    for (Integer port : allPorts) {
      ServerLogger.log(String.format("Sending sendAccept to %d", port));
      restService.sendAccept(request, port);
    }
  }

  @Override
  public void sendAccepted(RequestKey key, ProposalID proposalID, UserFile acceptedValue, String requestUUID) throws NotBoundException, RemoteException {
    AcceptedRequestPojo request = AcceptedRequestPojo.builder()
            .key(key)
            .fromUID(webServerPort)
            .proposalID(proposalID)
            .acceptedValue(acceptedValue)
            .requestUUID(requestUUID)
            .build();
    List<Integer> allPorts = allServers.getAllPorts();
    for (Integer port : allPorts) {
      ServerLogger.log(String.format("Sending sendAccepted to %d", port));
      restService.sendAccepted(request, port);
    }
  }

  @Override
  public void onResolution(RequestKey key, ProposalID proposalID, UserFile value, String requestUUID) throws NotBoundException, RemoteException {
    CommitRequestPojo request = CommitRequestPojo.builder()
            .key(key)
            .value(value)
            .requestUUID(requestUUID)
            .build();
    List<Integer> allPorts = allServers.getAllPorts();
    for (Integer port : allPorts) {
      ServerLogger.log(String.format("Sending onResolution to %d", port));
      restService.sendCommit(request,port);
    }
  }
}
