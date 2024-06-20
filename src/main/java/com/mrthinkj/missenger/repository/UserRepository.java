package com.mrthinkj.missenger.repository;

import com.mrthinkj.missenger.entity.User;
import com.mrthinkj.missenger.payload.Status;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByNickname(String nickname);
    List<User> findAllByStatus(Status online);
}
