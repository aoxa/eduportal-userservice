package io.zuppelli.userservice.service.impl;

import io.zuppelli.userservice.model.*;
import io.zuppelli.userservice.repository.RoleByNameRepository;
import io.zuppelli.userservice.repository.RoleRepository;
import io.zuppelli.userservice.repository.RolesByUserRepository;
import io.zuppelli.userservice.repository.UsersByRoleRepository;
import io.zuppelli.userservice.service.Builder;
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

    @Autowired
    private UsersByRoleRepository usersByRoleRepository;

    @Autowired
    private RoleByNameRepository roleByNameRepository;

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

    public void delete(UUID roleId) {

        usersByRoleRepository.findById(roleId)
                .ifPresent((ubr)->{
                    rolesByUserRepository.findAllById(ubr.getUserIds()).forEach((rbu)->{
                            rbu.getRoleIds().remove(roleId);
                            rolesByUserRepository.save(rbu);
                    });

                    usersByRoleRepository.delete(ubr);
                });


        roleRepository.findById(roleId).ifPresent(roleRepository::delete);
    }

    public Builder<Role> builder() {
        return this.new RoleBuilder();
    }

    private class RoleBuilder extends Builder<Role> {

        public RoleBuilder() {
            super(Role.class);
        }

        @Override
        protected void prebuild() {
            roleRepository.save(this.getObj());
        }

        @Override
        protected void postbuild() {
            Role role = this.getObj();
            RoleByName rbn = new RoleByName();

            rbn.setRoleId(role.getId());
            rbn.setName(role.getName());

            roleByNameRepository.save(rbn);
        }


    }
}
