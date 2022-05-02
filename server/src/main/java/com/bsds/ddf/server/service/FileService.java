package com.bsds.ddf.server.service;

import com.bsds.ddf.server.entities.UserFile;
import com.bsds.ddf.server.repository.FileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FileService {

  @Autowired
  private FileRepository fileRepository;

  public List<UserFile> getAllFiles(String username) {
    return fileRepository.findAllFilesByUsername(username);
  }

  public UserFile addUserFile(UserFile userFile) {
    return fileRepository.save(userFile);
  }

  public UserFile getFile(String fileName, String username) {
    List<UserFile> allFiles = getAllFiles(username);

    Optional<UserFile> userFileOptional = allFiles.stream().filter(file -> file.getFilename().equals(fileName))
            .findFirst();

    if (userFileOptional.isPresent()) {
      return userFileOptional.get();
    } else {
      return null;
    }
  }

  public void deleteFileForUser(String username, String fileName) {
    UserFile fileToDelete = getFile(fileName, username);
    fileRepository.delete(fileToDelete);
  }

  public void renameFileForUser(String username, String fileName, String newFileName) {
    UserFile fileToRename = getFile(username, fileName);
    fileRepository.save(fileToRename);
  }

  public List<UserFile> getAllDBFiles() {
    return (List<UserFile>) fileRepository.findAll();
  }
}
