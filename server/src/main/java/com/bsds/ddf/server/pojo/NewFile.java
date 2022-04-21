package com.bsds.ddf.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewFile {
  private String email;

  private String fileName;

  private String fileContent;
}
