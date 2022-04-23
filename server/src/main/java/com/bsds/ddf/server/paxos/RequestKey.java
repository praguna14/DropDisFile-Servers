package com.bsds.ddf.server.paxos;

import lombok.Data;

@Data
public class RequestKey {
  private String username;

  private String fileName;
}
