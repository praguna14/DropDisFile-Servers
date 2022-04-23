package com.bsds.ddf.server.paxos;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class to represent a request
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Request implements Serializable {
  private String requestType = null;
  private RequestKey key = null;
  private String value = null;

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Request{");
    sb.append("requestType='").append(requestType).append('\'');
    sb.append(", key='").append(key).append('\'');
    sb.append(", value='").append(value).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
