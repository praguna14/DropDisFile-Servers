package com.bsds.ddf.server;

import com.bsds.ddf.server.paxos.AllServers;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
  @Autowired
  private AllServers allServers;

  private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

  @Scheduled(fixedRate = 2000)
  public void reportCurrentTime() {
    log.info("Servers refreshed. The time is now {}", dateFormat.format(new Date()));
    allServers.refreshServers();
    log.info("Total Servers:  {}", allServers.getAllPorts().size());
  }
}