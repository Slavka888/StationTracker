package org.example.stationtracker.service;

import jakarta.transaction.Transactional;
import org.example.stationtracker.entity.User;
import org.example.stationtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void deleteByUserId(Long id){
        userRepository.deleteById(id);
    }
}
