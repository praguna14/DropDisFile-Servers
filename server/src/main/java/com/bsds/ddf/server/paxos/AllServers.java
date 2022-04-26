package com.bsds.ddf.server.paxos;

import com.bsds.ddf.server.ServerLogger;
import com.bsds.ddf.server.service.RestService;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AllServers {
  private List<Integer> allPorts;
  private RestService restService;

  public AllServers(RestService restService) {
    this.restService = restService;
    List<Integer> servers = null;
    try {
      servers = restService.getServers();
      servers = servers.stream().map( s -> s + 1000).collect(Collectors.toList());
    } catch(Exception e){
      System.out.println("Error occurred while hitting CMS:"+e.getMessage());
    }
    if(servers != null){
      allPorts = servers;
    } else{
      allPorts = new ArrayList<>();
    }
  }

  public List<Integer> getAllPorts() {
    return allPorts;
  }

  public void refreshServers() {
    List<Integer> servers = null;
    try {
      servers = restService.getServers();
      servers = servers.stream().map( s -> s + 1000).collect(Collectors.toList());
    } catch(Exception e){
      ServerLogger.log("Error occurred while hitting CMS:"+e.getMessage());
    }
    if(servers != null){
      allPorts = restService.getServers();
    }
  }
}
