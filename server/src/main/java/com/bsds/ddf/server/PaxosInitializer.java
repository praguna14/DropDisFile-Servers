package com.bsds.ddf.server;

import com.bsds.ddf.server.service.RestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

@Configuration
public class PaxosInitializer {

  @Autowired
  private ServletWebServerApplicationContext webServerAppCtxt;

  @Bean("serverPort")
  public int getServerPort(ServletWebServerApplicationContext webServerAppCtxt){
    return webServerAppCtxt.getWebServer().getPort();
  }

  @Bean("rmiPort")
  @DependsOn({"serverPort"})
  public int getRMIPort(@Qualifier("serverPort") int serverPort) {
    return serverPort + 1000;
  }

  @Bean("allPorts")
  @RequestScope
  public List<Integer> getAllPorts(RestService restService) {
    return restService.getServers();
  }
}
