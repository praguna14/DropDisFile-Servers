package com.bsds.ddf.server.service;

import com.bsds.ddf.server.entities.User;
import com.bsds.ddf.server.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

  @Autowired
  UserRepository userRepository;

  public User checkIsValidUser(String email, String password){
    return userRepository.findFirstByEmailAndPassword(email, password);
  }

  public List<User> getAllUsers(){
    return (List<User>) userRepository.findAll();
  }

  public User addUser(User user){
    userRepository.save(user);

    return userRepository.findFirstByEmailAndPassword(user.getEmail(), user.getPassword());
  }

  public String getUserLocation(String email){
    return userRepository.findFirstByEmail(email).getUserLocation();
  }

}
