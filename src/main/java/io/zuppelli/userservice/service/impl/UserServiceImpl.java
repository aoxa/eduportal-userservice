package io.zuppelli.userservice.service.impl;

import io.zuppelli.userservice.model.Role;
import io.zuppelli.userservice.model.User;
import io.zuppelli.userservice.model.UserByUsername;
import io.zuppelli.userservice.repository.UserByEmailRepository;
import io.zuppelli.userservice.repository.UserByUsernameRepository;
import io.zuppelli.userservice.repository.UserRepository;
import io.zuppelli.userservice.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserByEmailRepository userByEmailRepository;

    @Autowired
    private UserByUsernameRepository usernameRepository;

    public Optional<User> find(UUID id) {
        return userRepository.findById(id);
    }

    public Optional<User> find(String email) {
        return userByEmailRepository.getByEmail(email);
    }

    public User persist(User user) {
        user = userRepository.save(user);
        if(! StringUtils.isBlank(user.getUsername())) {
            UserByUsername ubu = new UserByUsername();
            ubu.setUserId(user.getId());
            ubu.setUsername(user.getUsername());
            usernameRepository.save(ubu);
        }
        return user;
    }

    public Slice<User> find(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}
