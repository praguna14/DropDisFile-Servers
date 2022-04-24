package com.bsds.ddf.server.paxos;

import com.bsds.ddf.server.entities.UserFile;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Class to represent a request
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Request implements Serializable {
  private String requestType;
  private RequestKey key ;
  private UserFile value ;
}
