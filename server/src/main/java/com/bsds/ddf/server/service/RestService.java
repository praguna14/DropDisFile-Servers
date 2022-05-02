package com.bsds.ddf.server.service;

import com.bsds.ddf.server.entities.UserFile;
import com.bsds.ddf.server.pojo.AcceptRequestPojo;
import com.bsds.ddf.server.pojo.AcceptedRequestPojo;
import com.bsds.ddf.server.pojo.CommitRequestPojo;
import com.bsds.ddf.server.pojo.PreparePojo;
import com.bsds.ddf.server.pojo.PromisePOJO;

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

  public void sendPrepare(PreparePojo request, int port){
    String url = String.format("http://localhost:%d/receivePrepare", port);
    try {
      this.restTemplate.postForEntity(url, request, Object.class);
    }catch(Exception ex){
      System.out.println("Error while sending request:"+url);
    }
  }

  public void sendPromise(PromisePOJO request, int port){
    String url = String.format("http://localhost:%d/receivePromise", port);
    try {
      this.restTemplate.postForEntity(url, request, Object.class);
    }catch (Exception ex){
      System.out.println("Error while sending request:"+url);
    }
  }

  public void sendAccept(AcceptRequestPojo request, Integer port) {
    String url = String.format("http://localhost:%d/receiveAccept", port);
    try {
      this.restTemplate.postForEntity(url, request, Object.class);
    } catch(Exception ex){
      System.out.println("Error while sending request:"+url);
    }
  }

  public void sendAccepted(AcceptedRequestPojo request, Integer port) {
    String url = String.format("http://localhost:%d/receiveAccepted", port);
    try {
      this.restTemplate.postForEntity(url, request, Object.class);
    } catch(Exception ex){
      System.out.println("Error while sending request:"+url);
    }
  }

  public void sendCommit(CommitRequestPojo request, Integer port) {
    String url = String.format("http://localhost:%d/commit", port);
    try {
      this.restTemplate.postForEntity(url, request, Object.class);
    } catch(Exception ex){
      System.out.println("Error while sending request:"+url);
    }
  }

  public List<UserFile> fetchAllFiles(Integer port){
    String url = String.format("http://localhost:%d/files/all", port);
    return Arrays.asList(this.restTemplate.getForObject(url, UserFile[].class));
  }

}
