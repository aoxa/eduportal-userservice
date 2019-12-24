package io.zuppelli.userservice.service;

import io.zuppelli.userservice.model.Role;
import io.zuppelli.userservice.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<User> find(String email);

    Optional<User> find(UUID id);

    User persist(User user);
}
