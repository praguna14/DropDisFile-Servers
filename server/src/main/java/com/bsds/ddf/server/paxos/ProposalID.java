package com.bsds.ddf.server.paxos;

import java.io.Serializable;

public class ProposalID implements Serializable {
  private static final long serialVersionUID = 1000000007;
  private int number;
  private final String uid;

  public ProposalID(int number, String uid) {
    this.number = number;
    this.uid = uid;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ProposalID{");
    sb.append("number=").append(number);
    sb.append(", uid='").append(uid).append('\'');
    sb.append('}');
    return sb.toString();
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public void incrementNumber() {
    this.number += 1;
  }

  public String getUID() {
    return uid;
  }

  public int compare(ProposalID rhs) {
    if (equals(rhs))
      return 0;
    if (number < rhs.number || (number == rhs.number && uid.compareTo(rhs.uid) < 0))
      return -1;
    return 1;
  }

  public boolean isGreaterThan(ProposalID rhs) {
    return compare(rhs) > 0;
  }

  public boolean isLessThan(ProposalID rhs) {
    return compare(rhs) < 0;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + number;
    result = prime * result + ((uid == null) ? 0 : uid.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ProposalID other = (ProposalID) obj;
    if (number != other.number)
      return false;
    if (uid == null) {
      if (other.uid != null)
        return false;
    } else if (!uid.equals(other.uid))
      return false;
    return true;
  }


}
