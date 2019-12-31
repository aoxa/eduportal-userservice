package io.zuppelli.userservice.service.impl;

import io.zuppelli.userservice.model.*;
import io.zuppelli.userservice.repository.*;
import io.zuppelli.userservice.service.Builder;
import io.zuppelli.userservice.service.GroupService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    private GroupsByRoleRepository groupsByRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

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

    public GroupBuilder builder() {
        return this.new GroupBuilder(roleRepository, groupsByRoleRepository, groupRepository);
    }

    private class GroupBuilder implements Builder<Group> {
        private Group group = new Group();

        private final RoleRepository roleRepository;
        private final GroupsByRoleRepository groupsByRoleRepository;
        private final GroupRepository groupRepository;

        GroupBuilder(RoleRepository roleRepository, GroupsByRoleRepository groupsByRoleRepository, GroupRepository groupRepository) {
            this.roleRepository = roleRepository;
            this.groupsByRoleRepository = groupsByRoleRepository;
            this.groupRepository = groupRepository;
        }

        public Builder<Group> add(String method, Object content) {
            if( null == group) throw new UnsupportedOperationException();

            try {
                if(! method.startsWith("set")) {
                    StringBuilder sb = new StringBuilder("set");
                    sb.append(StringUtils.capitalize(method));
                    method = sb.toString();
                }
                Method m = Group.class.getMethod(method, content.getClass());
                m.invoke(group, content);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                throw new UnsupportedOperationException();
            }

            return this;
        }

        public Group build() {
            if( null == group) throw new UnsupportedOperationException();
            try {
                Role role = new Role();
                role.setName(group.getName().replace(" ", "_"));
                role = roleRepository.save(role);

                group.setPrimaryRole(role.getId());

                group = groupRepository.save(group);

                groupsByRoleRepository.save(new GroupByRole(role.getId(), group.getId()));

                return group;
            } finally {
                group = null;
            }
        }
    }
}
