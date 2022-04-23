package com.bsds.ddf.server.controller;

import com.bsds.ddf.server.entities.UserFile;
import com.bsds.ddf.server.service.FileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FileController {

  @Autowired
  private FileService fileService;

  //Gets all files for a user
  @GetMapping("/files")
  @ResponseBody
  public List<UserFile> getAllFiles(@RequestParam String username) {
    return fileService.getAllFiles(username);
  }

  //Gets only requested file for the user
  @GetMapping("/file")
  @ResponseBody
  public UserFile getFile(@RequestParam String username, @RequestParam String fileName) {
    return fileService.getFile(fileName, username);
  }

  //Adds a new file for the user
  @PostMapping("/file")
  @ResponseBody
  public UserFile addFile(@RequestBody UserFile file) throws Exception {
    fileService.addUserFile(file);

    return fileService.getFile(file.getFileName(), file.getUsername());
  }

  //Deletes a file for the user
  @DeleteMapping("/delete")
  @ResponseBody
  public void getAllFiles(@RequestParam String username, @RequestParam String fileName) {
    fileService.deleteFileForUser(username, fileName);
  }

  //Renames a file for user
  @PutMapping("/rename")
  @ResponseBody
  public void renameFile(@RequestParam String username, @RequestParam String fileName, @RequestParam String newFileName) {
    //TO DO
    fileService.renameFileForUser(username, fileName, newFileName);
  }
}
