package io.zuppelli.userservice.service.impl;

import io.zuppelli.userservice.model.Role;
import io.zuppelli.userservice.model.RolesByUser;
import io.zuppelli.userservice.model.User;
import io.zuppelli.userservice.repository.RoleRepository;
import io.zuppelli.userservice.repository.RolesByUserRepository;
import io.zuppelli.userservice.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RolesByUserRepository rolesByUserRepository;

    @Override
    public Optional<Role> find(UUID id) {
        return roleRepository.findById(id);
    }

    public void addRole(User user, Role role) {
        Optional<RolesByUser> rolesByUser = rolesByUserRepository.findById(user.getId());

        RolesByUser rbu = rolesByUser.orElseGet(()->{
            RolesByUser r = new RolesByUser();
            r.setUserId(user.getId());
            return r;
        });

        rbu.getRoleIds().add(role.getId());

        rolesByUserRepository.save(rbu);
    }

    public List<Role> getRoles(User user) {
        RolesByUser rolesByUser = rolesByUserRepository.findById(user.getId())
                .orElseGet(()->new RolesByUser());

        return rolesByUser.getRoleIds().isEmpty()? Collections.emptyList() :
                roleRepository.findAllById(rolesByUser.getRoleIds());
    }
}
