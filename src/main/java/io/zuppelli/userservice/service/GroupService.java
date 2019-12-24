package io.zuppelli.userservice.service;

import io.zuppelli.userservice.model.Group;
import io.zuppelli.userservice.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupService {
    Optional<Group> find(UUID group);

    List<Group> findGroups(User user);

    void addGroup(User user, Group group);
}
