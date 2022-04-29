package com.bsds.ddf.server.entities;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFileCompositeKey implements Serializable {
  private String username;
  private String filename;
}
