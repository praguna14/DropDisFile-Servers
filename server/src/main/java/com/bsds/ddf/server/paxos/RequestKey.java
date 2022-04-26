package com.bsds.ddf.server.paxos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestKey {
  private String username;

  private String fileName;
}
