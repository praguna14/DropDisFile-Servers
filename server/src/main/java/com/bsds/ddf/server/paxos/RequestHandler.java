package com.bsds.ddf.server.paxos;

import com.bsds.ddf.server.ServerLogger;
import com.neu.bsds.client.Request;

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
      switch (request.getRequestType()) {
        case "GET":
          response.set(processGet(request.getKey()));
          break;
        case "PUT":
        case "DELETE":
          if (request.getRequestType().equals("DELETE")) {
            RequestKey key = request.getKey();
            if (!KeyValue.contains(key)) {
              response.set(new Response(false, "Key requested to be deleted not present"));
            }
          }

          if (response.get() == null) {
            try {
              String value = (request.getRequestType().equals("PUT") ? request.getValue() : "");
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
            response.set(new Response(successful, errorMessage));
          }
          break;
        default:
          ServerLogger.log("Invalid/malformed request received: " + request);
          response.set(new Response(false, "Invalid request."));
      }


    };

    // run the thread created.
    runnable.run();
    final Response res = response.get();

    ServerLogger.log(String.format("Server responded with the response: %s", res));

    return res;
  }

  /**
   * Synchronized method to handle PUT requests having a key and value pairs. Stores the same in the
   * key value store.
   */
  public synchronized Response processPut(String key, String value) {
    KeyValue.put(key, value);

    Response response = new Response(true);

    return response;
  }

  /**
   * Synchronized method to handle GET requests having a key. Returns the current value of the key
   * from the key value store.
   */
  public synchronized Response processGet(RequestKey key) {
    Response response = null;

    String value = KeyValue.get(key);
    if (value != null) {
      response = new Response(true, value);
    } else {
      response = new Response(false);
    }

    return response;
  }

  /**
   * Synchronized method to handle PUT requests having a key. Returns a boolean indicating if delete
   * was successful or not.
   */
  public synchronized Response processDelete(String key) {
    boolean isSuccessful = KeyValue.delete(key);

    Response response = new Response(isSuccessful);

    return response;
  }
}
