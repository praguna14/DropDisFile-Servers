package com.bsds.ddf.server.paxos;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Response sent from the server to client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Response implements Serializable {
  private Boolean successful;
  private String value;
  private String error;
}
