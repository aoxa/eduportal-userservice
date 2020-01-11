package io.zuppelli.userservice.service.impl;

import io.zuppelli.userservice.model.*;
import io.zuppelli.userservice.repository.*;
import io.zuppelli.userservice.service.Builder;
import io.zuppelli.userservice.service.GroupService;
import io.zuppelli.userservice.service.RoleService;
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
    private GroupByNameRepository groupByNameRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private GroupRepository groupRepository;

    public Optional<Group> find(UUID id) {
        return groupRepository.findById(id);
    }


    public Optional<Group> find(String groupname) {
        return groupByNameRepository.getByName(groupname);
    }

    public List<Group> findGroups(User user) {

        Optional<GroupsByUser> optional = groupByUserRepository.findById(user.getId());
        if(!optional.isPresent()) {
            return Collections.emptyList();
        }

        GroupsByUser groupsByUser = optional.get();

        return groupRepository.findAllById(groupsByUser.getGroupIds());
    }

    public List<Group> findLike(String name) {
        return groupByNameRepository.like(name);
    }

    public void removeGroup(User user, Group group) {
        groupByUserRepository.findById(user.getId())
                .ifPresent(groupsByUser->{
                    groupsByUser.getGroupIds().remove(group.getId());
                    groupByUserRepository.save(groupsByUser);
                });
        usersByGroupRepository.findById(group.getId())
                .ifPresent(ubg->{
                    ubg.getUserIds().remove(user.getId());
                    usersByGroupRepository.save(ubg);
                });
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

    public void delete(Group group) {
        usersByGroupRepository.findById(group.getId())
                .ifPresent((ubg)->{
                    groupByUserRepository.findAllById(ubg.getUserIds()).forEach((gbu)->{
                        gbu.getGroupIds().remove(group.getId());
                        groupByUserRepository.save(gbu);
                    });

                    usersByGroupRepository.delete(ubg);
                });

        groupsByRoleRepository.findById(group.getPrimaryRole())
                .ifPresent(groupsByRoleRepository::delete);

        roleService.delete(group.getPrimaryRole());

        groupRepository.delete(group);
    }

    private class GroupBuilder extends Builder<Group> {
        private final RoleRepository roleRepository;
        private final GroupsByRoleRepository groupsByRoleRepository;
        private final GroupRepository groupRepository;

        GroupBuilder(RoleRepository roleRepository, GroupsByRoleRepository groupsByRoleRepository, GroupRepository groupRepository) {
            super(Group.class);
            this.roleRepository = roleRepository;
            this.groupsByRoleRepository = groupsByRoleRepository;
            this.groupRepository = groupRepository;
        }


        public void prebuild() {
                Group group = this.getObj();

                Role role = new Role();
                role.setName(group.getName().replace(" ", "_"));
                role = roleRepository.save(role);

                group.setPrimaryRole(role.getId());

                group = groupRepository.save(group);

                groupsByRoleRepository.save(new GroupByRole(role.getId(), group.getId()));
        }
    }
}
