package com.bsds.ddf.server.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(UserFileCompositeKey.class)
public class UserFile {

  @Id
  private String username;

  @Id
  private String filename;

  private String content;
}
