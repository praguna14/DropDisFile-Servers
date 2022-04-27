package com.bsds.ddf.server.pojo;

import com.bsds.ddf.server.entities.UserFile;
import com.bsds.ddf.server.paxos.ProposalID;
import com.bsds.ddf.server.paxos.RequestKey;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class AcceptRequestPojo implements Serializable {
  RequestKey key;
  String fromUID;
  ProposalID proposalID;
  UserFile value;
  String requestUUID;
}
