package com.bsds.ddf.server.paxos;

import com.bsds.ddf.server.service.RestService;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AllServers {
  private List<Integer> allPorts;
  private RestService restService;

  public AllServers(RestService restService) {
    this.restService = restService;
    List<Integer> servers = restService.getServers();
    if(servers != null){
      allPorts = restService.getServers();
    } else{
      allPorts = new ArrayList<>();
    }
  }

  public List<Integer> getAllPorts() {
    return allPorts;
  }

  public void refreshServers() {
    allPorts = restService.getServers();
  }
}
