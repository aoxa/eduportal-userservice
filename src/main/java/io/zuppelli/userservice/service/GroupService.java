package io.zuppelli.userservice.service;

import io.zuppelli.userservice.model.Group;
import io.zuppelli.userservice.model.User;

import java.util.List;

public interface GroupService {
    List<Group> findGroups(User user);

    void addGroup(User user, Group group);
}
