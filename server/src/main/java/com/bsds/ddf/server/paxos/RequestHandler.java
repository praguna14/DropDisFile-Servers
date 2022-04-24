package com.bsds.ddf.server.paxos;

import com.bsds.ddf.server.ServerLogger;
import com.bsds.ddf.server.entities.UserFile;
import com.bsds.ddf.server.service.FileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Abstract class containing all the implementation details of handling client requests. Clients can
 * call processRequest() method for sending requests to server.
 */

@Component
public class RequestHandler {
  @Value("${server.port}")
  private int webServerPort;

  @Autowired
  private Proposer proposer;

  @Autowired
  private FileService fileService;

  public static Map<String, String> activeRequests;

  public RequestHandler() {
    activeRequests = new ConcurrentHashMap<>();
  }

  /**
   * Generic request processing method to handle all request types. Each request gets run on a new
   * thread so multiple requests can be handled by the same server.
   *
   * @param request request to be handled
   * @return response the response for the request
   */
  public Response processRequest(Request request) {
    AtomicReference<Response> response = new AtomicReference<>();
    ServerLogger.log(String.format("Server got the request of type: %s with key: %s and value: %s",
            request.getRequestType(), request.getKey(), request.getValue()));

    // Creates new thread for processing each request
    Runnable runnable = () -> {
      boolean successful = true;
      String errorMessage = null;

      if (request.getRequestType().equals("DELETE")) {
        RequestKey key = request.getKey();
        if (!checkIfFilePresent(key.getUsername(), key.getFileName())) {
          Response responseContent  = Response.builder()
                  .successful(false)
                  .value("Key requested to be deleted not present")
                  .build();
          response.set(responseContent);
        }
      }

      if (response.get() == null) {
        try {
          UserFile value = (request.getRequestType().equals("PUT") ? request.getValue() : null);
          proposer.setProposal(request.getKey(), value);
        } catch (RemoteException e) {
          successful = false;
          errorMessage = e.getMessage();
          e.printStackTrace();
        }
        String requestUUID = String.valueOf(UUID.randomUUID());
        activeRequests.put(requestUUID, "In-Progress");
        int prepareTries = 5;
        while (prepareTries > 0 && !activeRequests.get(requestUUID).equals("Completed")) {
          prepareTries--;
          if (successful) {
            try {
              proposer.prepare(request.getKey(), requestUUID);
            } catch (RemoteException | NotBoundException e) {
              ServerLogger.log("Error occurred while preparing value for" + request.getRequestType()
                      + "request: " + request);
              ServerLogger.log("Error cause: " + e.getMessage());
              errorMessage = e.getMessage();
              successful = false;
            }
          }
          if (successful) {
            int triesLeft = 5;
            while (!activeRequests.get(requestUUID).equals("Completed") && triesLeft != 0) {
              try {
                triesLeft--;
                Thread.sleep(300);
              } catch (InterruptedException e) {
                ServerLogger.log("Error occurred while waiting for consensus");
                e.printStackTrace();
                errorMessage = e.getMessage();
                successful = false;
                break;
              }
            }
          }
        }
        if (prepareTries == 0 && !activeRequests.get(requestUUID).equals("Completed")) {
          errorMessage = "Could not reach consensus. Request Timed Out.";
          successful = false;
        }
        Response responseContent  = Response.builder()
                .successful(successful)
                .error(errorMessage)
                .build();
        response.set(responseContent);
      }

    };

    // run the thread created.
    runnable.run();
    final Response res = response.get();

    ServerLogger.log(String.format("Server responded with the response: %s", res));

    return res;
  }

  private boolean checkIfFilePresent(String userName, String fileName){
    UserFile userFile = fileService.getFile(fileName, userName);
    return userFile == null;
  }
}
