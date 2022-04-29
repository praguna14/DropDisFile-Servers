package com.bsds.ddf.server.repository;

import com.bsds.ddf.server.entities.UserFile;
import com.bsds.ddf.server.entities.UserFileCompositeKey;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FileRepository extends CrudRepository<UserFile, UserFileCompositeKey> {
  public List<UserFile> findAllFilesByUsername(String username);
}
