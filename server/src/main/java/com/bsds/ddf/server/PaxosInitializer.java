package com.bsds.ddf.server;

import com.bsds.ddf.server.service.RestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${server.port}")
  private String port;


  @Bean("serverPort")
  public int getServerPort(ServletWebServerApplicationContext webServerAppCtxt) {
    return Integer.parseInt(port);
  }
}
