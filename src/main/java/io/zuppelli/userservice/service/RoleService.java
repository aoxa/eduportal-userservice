package io.zuppelli.userservice.service;

import io.zuppelli.userservice.model.Role;
import io.zuppelli.userservice.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleService {

    Optional<Role> find(UUID id);

    void addRole(User user, Role role);

    List<Role> getRoles(User user);
}
