package com.bsds.ddf.server.repository;

import com.bsds.ddf.server.entities.UserFile;
import com.bsds.ddf.server.entities.pk.FilePK;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FileRepository extends CrudRepository<UserFile, String> {
  public List<UserFile> findAllFilesByEmail(String email);

  public UserFile findFirstByEmailAndFileName(String email, String fileName);
}
