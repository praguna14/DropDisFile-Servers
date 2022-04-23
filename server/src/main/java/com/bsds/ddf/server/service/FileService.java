package com.bsds.ddf.server.service;

import com.bsds.ddf.server.entities.UserFile;
import com.bsds.ddf.server.repository.FileRepository;

import org.apache.catalina.User;
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

  public UserFile getFile(String fileName, String username){
    return fileRepository.findFirstByUsernameAndFileName(username, fileName);
  }

  public void deleteFileForUser(String username, String fileName) {
    UserFile fileToDelete = fileRepository.findFirstByUsernameAndFileName(username, fileName);
    fileRepository.delete(fileToDelete);
  }

  public void renameFileForUser(String username, String fileName, String newFileName) {
    UserFile fileToRename = fileRepository.findFirstByUsernameAndFileName(username, fileName);
    fileRepository.save(fileToRename);
  }
}
