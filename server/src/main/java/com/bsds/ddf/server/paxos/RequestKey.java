package com.bsds.ddf.server.paxos;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestKey implements Serializable {
  private String username;

  private String fileName;
}
