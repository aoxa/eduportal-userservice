package io.zuppelli.userservice.service.impl;

import io.zuppelli.userservice.model.Group;
import io.zuppelli.userservice.model.GroupsByUser;
import io.zuppelli.userservice.model.User;
import io.zuppelli.userservice.model.UsersByGroup;
import io.zuppelli.userservice.repository.GroupByUserRepository;
import io.zuppelli.userservice.repository.GroupRepository;
import io.zuppelli.userservice.repository.UsersByGroupRepository;
import io.zuppelli.userservice.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GroupServiceImpl implements GroupService {
    @Autowired
    private GroupByUserRepository groupByUserRepository;
    @Autowired
    private UsersByGroupRepository usersByGroupRepository;

    @Autowired
    private GroupRepository groupRepository;

    public Optional<Group> find(UUID id) {
        return groupRepository.findById(id);
    }

    public List<Group> findGroups(User user) {

        Optional<GroupsByUser> optional = groupByUserRepository.findById(user.getId());
        if(!optional.isPresent()) {
            return Collections.emptyList();
        }

        GroupsByUser groupsByUser = optional.get();

        return groupRepository.findAllById(groupsByUser.getGroupIds());
    }

    public void addGroup(User user, Group group) {
        Optional<GroupsByUser> optional = groupByUserRepository.findById(user.getId());
        GroupsByUser groupsByUser = optional.orElseGet(()->{
            GroupsByUser gu = new GroupsByUser();
            gu.setUserId(user.getId());
            return gu;
        });
        if(!groupsByUser.getGroupIds().contains(group.getId())) {
            groupsByUser.getGroupIds().add(group.getId());

            groupByUserRepository.save(groupsByUser);

            UsersByGroup usersByGroup = usersByGroupRepository.findById(group.getId()).orElseGet(()->{
                UsersByGroup ubg = new UsersByGroup();
                ubg.setGroupId(group.getId());
                return ubg;
            });

            usersByGroup.getUserIds().add(user.getId());

            usersByGroupRepository.save(usersByGroup);
        }
    }
}
