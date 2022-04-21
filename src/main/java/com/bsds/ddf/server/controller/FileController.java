package com.bsds.ddf.server.controller;

import com.bsds.ddf.server.entities.UserFile;
import com.bsds.ddf.server.pojo.NewFile;
import com.bsds.ddf.server.service.FileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FileController {

  @Autowired
  private FileService fileService;

  @GetMapping("/file")
  @ResponseBody
  public List<UserFile> getAllFiles(@RequestParam String userId, @RequestParam String email) {
    return fileService.getAllFiles(email);
  }

  @PostMapping("/file")
  @ResponseBody
  public UserFile addFile(@RequestBody NewFile newFile) throws Exception {
    fileService.addUserFile(newFile.getEmail(), newFile.getFileName(), newFile.getFileContent());

    return fileService.getFile(newFile.getFileName(), newFile.getEmail());
  }
}
