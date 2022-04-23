package com.bsds.ddf.server.paxos;

import java.io.Serializable;

/**
 * Response sent from the server to client.
 */
public class Response implements Serializable {
  private Boolean successful;
  private String value;
  private String error;

  public Response() {
    successful = null;
    value = null;
    error = null;
  }

  public Response(boolean successful, String value) {
    this.successful = successful;
    this.value = value;
  }


  public Response(boolean successful) {
    this.successful = successful;
  }

  public boolean isSuccessful() {
    return successful;
  }

  public void setSuccessful(boolean successful) {
    this.successful = successful;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getError(){
    return this.error;
  }

  public void setError(String error){
    this.error = error;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Response{");
    sb.append("successful=").append(successful);
    sb.append(", value='").append(value).append('\'');
    sb.append(", error='").append(error).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
