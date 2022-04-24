package com.bsds.ddf.server.paxos;

import com.bsds.ddf.server.service.RestService;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllServers {
  private List<Integer> allPorts;
  private RestService restService;

  public AllServers(RestService restService) {
    this.restService = restService;
    allPorts = restService.getServers();
  }

  public List<Integer> getAllPorts() {
    return allPorts;
  }

  public void refreshServers() {
    allPorts = restService.getServers();
  }
}
