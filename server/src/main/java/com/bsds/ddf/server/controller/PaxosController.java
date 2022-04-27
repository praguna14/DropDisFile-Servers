package com.bsds.ddf.server.controller;

import com.bsds.ddf.server.paxos.Acceptor;
import com.bsds.ddf.server.paxos.Learner;
import com.bsds.ddf.server.paxos.Proposer;
import com.bsds.ddf.server.pojo.AcceptRequestPojo;
import com.bsds.ddf.server.pojo.AcceptedRequestPojo;
import com.bsds.ddf.server.pojo.CommitRequestPojo;
import com.bsds.ddf.server.pojo.PreparePojo;
import com.bsds.ddf.server.pojo.PromisePOJO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

@RestController
public class PaxosController {

  private Learner learner;
  private Acceptor acceptor;
  private Proposer proposer;

  @Autowired
  public PaxosController(Proposer proposer, Acceptor acceptor, Learner learner){
    this.proposer = proposer;
    this.learner = learner;
    this.acceptor = acceptor;
  }

  @PostMapping("/receivePromise")
  public void receivePromise(@RequestBody PromisePOJO receivePromise) throws NotBoundException, RemoteException {
    proposer.receivePromise(receivePromise.getKey(), receivePromise.getFromUID(), receivePromise.getProposalID(),
            receivePromise.getPrevAcceptedID(), receivePromise.getPrevAcceptedValue(), receivePromise.getRequestUUID());
  }

  @PostMapping("/receivePrepare")
  public void receivePrepare(@RequestBody PreparePojo preparePojo) throws NotBoundException, RemoteException {
    acceptor.receivePrepare(preparePojo.getKey(), preparePojo.getFromUID(), preparePojo.getProposalID(),
            preparePojo.getRequestUUID());
  }

  @PostMapping("/receiveAccept")
  public void receiveAccept(@RequestBody AcceptRequestPojo requestPojo) throws NotBoundException, RemoteException {
    acceptor.receiveAcceptRequest(requestPojo.getKey(), requestPojo.getFromUID(), requestPojo.getProposalID(),
            requestPojo.getValue(), requestPojo.getRequestUUID());
  }

  @PostMapping("/receiveAccepted")
  public void receiveAccepted(@RequestBody AcceptedRequestPojo requestPojo) throws NotBoundException, RemoteException {
    learner.receiveAccepted(requestPojo.getKey(), requestPojo.getFromUID(), requestPojo.getProposalID(),
            requestPojo.getAcceptedValue(), requestPojo.getRequestUUID());
  }

  @PostMapping("/commit")
  public void commit(@RequestBody CommitRequestPojo requestPojo) throws RemoteException {
    learner.commit(requestPojo.getKey(), requestPojo.getValue(), requestPojo.getRequestUUID());
  }
}
