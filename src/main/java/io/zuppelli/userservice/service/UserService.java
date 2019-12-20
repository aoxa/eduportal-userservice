package io.zuppelli.userservice.service;

import io.zuppelli.userservice.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<User> findUser(String email);

    Optional<User> findUser(UUID id);

    User persist(User user);
}
