package com.bsds.ddf.server.repository;

import com.bsds.ddf.server.entities.UserFile;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FileRepository extends CrudRepository<UserFile, String> {
  public List<UserFile> findAllFilesByUsername(String username);

  public UserFile findFirstByUsernameAndFileName(String email, String fileName);
}
