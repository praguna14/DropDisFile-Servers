package com.bsds.ddf.server.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class RestService {

  private final RestTemplate restTemplate;

  public RestService(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  public List<Integer> getServers() {
    String url = "http://localhost:8081/servers";
    return Arrays.asList(this.restTemplate.getForObject(url, Integer[].class));
  }
}
