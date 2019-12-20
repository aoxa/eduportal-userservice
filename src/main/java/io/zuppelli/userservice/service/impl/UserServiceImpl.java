package io.zuppelli.userservice.service.impl;

import io.zuppelli.userservice.model.User;
import io.zuppelli.userservice.repository.UserByEmailRepository;
import io.zuppelli.userservice.repository.UserRepository;
import io.zuppelli.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserByEmailRepository userByEmailRepository;

    public Optional<User> findUser(UUID id) {
        return userRepository.findById(id);
    }

    public Optional<User> findUser(String email) {
        return userByEmailRepository.getByEmail(email);
    }

    public User persist(User user) {
        return userRepository.save(user);
    }
}
