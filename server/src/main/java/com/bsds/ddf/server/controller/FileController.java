package com.bsds.ddf.server.controller;

import com.bsds.ddf.server.entities.UserFile;
import com.bsds.ddf.server.paxos.AllServers;
import com.bsds.ddf.server.paxos.Request;
import com.bsds.ddf.server.paxos.RequestHandler;
import com.bsds.ddf.server.paxos.RequestKey;
import com.bsds.ddf.server.paxos.Response;
import com.bsds.ddf.server.service.FileService;

import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins="*")
public class FileController {
  private FileService fileService;
  private AllServers allServers;
  private RequestHandler requestHandler;

  public FileController(FileService fileService, AllServers allServers, RequestHandler requestHandler) {
    this.fileService = fileService;
    this.allServers = allServers;
    this.requestHandler = requestHandler;
  }

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
    Request request = Request.builder()
            .requestType("PUT")
            .key(new RequestKey(file.getUsername(), file.getFilename()))
            .value(file)
            .build();

    Response response = this.requestHandler.processRequest(request);

    if(response.getSuccessful()){
      return fileService.getFile(file.getFilename(), file.getUsername());
    } else{
      throw new Exception("Could not save file");
    }
  }

  //Deletes a file for the user
  @DeleteMapping("/delete")
  @ResponseBody
  public void getAllFiles(@RequestParam String username, @RequestParam String filename) throws Exception {
    Request request = Request.builder()
            .requestType("DELETE")
            .key(new RequestKey(username, filename))
            .build();

    Response response = this.requestHandler.processRequest(request);

    if(!response.getSuccessful()){
      throw new Exception("Could not delete file");
    }
  }

  //Renames a file for user
  @PutMapping("/rename")
  @ResponseBody
  public void renameFile(@RequestParam String username, @RequestParam String fileName, @RequestParam String newFileName) {
    //TO DO
    fileService.renameFileForUser(username, fileName, newFileName);
  }

  @GetMapping("/files/all")
  @ResponseBody
  public List<UserFile> fetchAllFiles(){
    return fileService.getAllDBFiles();
  }
}
