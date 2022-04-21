package com.bsds.ddf.server.repository;

import com.bsds.ddf.server.entities.User;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
  public User findFirstByEmailAndPassword(String email, String password);

  public User findFirstByEmail(String email);
}
