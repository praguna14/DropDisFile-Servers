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

  @Autowired
  private StorageService storageService;

  @Autowired
  private UserService userService;

  public List<UserFile> getAllFiles(String email) {
    return fileRepository.findAllFilesByEmail(email);
  }


  public UserFile addUserFile(String email, String fileName, String fileContent) throws Exception {
    String fileLocation = userService.getUserLocation(email);
    UserFile userFile = UserFile.builder()
            .fileName(fileName)
            .fileLocation(fileLocation)
            .build();
    boolean fileStoredFlag = storageService.storeFile(fileLocation, fileName, fileContent);

    if(fileStoredFlag) {
      return fileRepository.save(userFile);
    } else{
      throw new Exception("File could not be saved");
    }
  }

  public UserFile getFile(String fileName, String email){
    return fileRepository.findFirstByEmailAndFileName(email, fileName);
  }
}
