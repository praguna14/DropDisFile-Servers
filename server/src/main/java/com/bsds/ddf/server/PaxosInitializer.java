package com.bsds.ddf.server;

import com.bsds.ddf.server.service.RestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaxosInitializer {

  @Autowired
  RestService restService;

  @Value("${server.port}")
  private int webServerPort;

  @Bean("rmiPort")
  public int getRMIPort() {
    return webServerPort + 1000;
  }

  @Bean("allPorts")
  public List<Integer> getAllPorts() {
    return restService.getServers();
  }
}
