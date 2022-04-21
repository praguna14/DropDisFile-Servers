package com.bsds.ddf.server.controller;

import com.bsds.ddf.server.entities.User;
import com.bsds.ddf.server.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

	@Autowired
	private UserService userService;

  @GetMapping("/users")
  public List<User> hello() {
    return userService.getAllUsers();
  }

  @PostMapping("/user")
  public User adduser(@RequestBody User user){
    return userService.addUser(user);
  }
}
