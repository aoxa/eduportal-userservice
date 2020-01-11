package io.zuppelli.userservice.service;

import io.zuppelli.userservice.model.Group;
import io.zuppelli.userservice.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupService {
    Optional<Group> find(UUID group);

    Optional<Group> find(String groupname);

    void delete(Group group);

    List<Group> findGroups(User user);

    List<Group> findLike(String name);

    void addGroup(User user, Group group);

    void removeGroup(User user, Group group);

    Builder<Group> builder();
}
