package com.bsds.ddf.server;

import com.bsds.ddf.server.service.RestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestContextListener;

import java.util.List;

@Configuration
public class PaxosInitializer {

  @Bean("serverPort")
  public int getServerPort(ServletWebServerApplicationContext webServerAppCtxt) {
    return webServerAppCtxt.getWebServer().getPort();
  }

  @Bean("rmiPort")
  @DependsOn({"serverPort"})
  public int getRMIPort(@Qualifier("serverPort") int serverPort) {
    return serverPort + 1000;
  }

  @Bean
  public RequestContextListener requestContextListener(){
    return new RequestContextListener();
  }

//  @Bean("allPorts")
//  @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
//  public List<Integer> getAllPorts(RestService restService) {
//    return restService.getServers();
//  }
}
