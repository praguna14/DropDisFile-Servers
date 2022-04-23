package com.bsds.ddf.server.service;

import com.bsds.ddf.server.entities.UserFile;
import com.bsds.ddf.server.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileService {

  @Autowired
  private FileRepository fileRepository;

  public List<UserFile> getAllFiles(String username) {
    return fileRepository.findAllFilesByUsername(username);
  }


  public UserFile addUserFile(UserFile userFile) throws Exception {
    return fileRepository.save(userFile);
  }

  public UserFile getFile(String fileName, String email){
    return fileRepository.findFirstByUsernameAndFileName(email, fileName);
  }
}
