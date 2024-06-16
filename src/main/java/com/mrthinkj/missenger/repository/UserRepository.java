package com.mrthinkj.missenger.repository;

import com.mrthinkj.missenger.entity.User;
import com.mrthinkj.missenger.payload.Status;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    List<User> findAllByStatus(Status online);
}
