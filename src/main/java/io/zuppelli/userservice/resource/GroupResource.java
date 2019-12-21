package io.zuppelli.userservice.resource;

import io.zuppelli.userservice.model.Group;
import io.zuppelli.userservice.model.Role;
import io.zuppelli.userservice.repository.GroupRepository;
import io.zuppelli.userservice.repository.RoleRepository;
import io.zuppelli.userservice.repository.UsersByGroupRepository;
import io.zuppelli.userservice.repository.UsersByRoleRepository;
import io.zuppelli.userservice.resource.dto.GroupDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
public class GroupResource {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UsersByGroupRepository usersByGroupRepository;

    @Autowired
    private UsersByRoleRepository usersByRoleRepository;

    @PostMapping
    public Group add(@RequestBody GroupDTO dto) {
        Group group = new Group();
        group.setName(dto.getName());

        Role role = new Role();
        role.setName(dto.getName().replace(" ", "_"));
        roleRepository.save(role);

        group.setPrimaryRole(role.getId());
        groupRepository.save(group);

        return group;
    }

    @GetMapping("/{group}")
    public Group get(@PathVariable Group group){
        return group;
    }

    @PutMapping("/{group}")
    public Group update(@PathVariable Group group, @RequestBody GroupDTO dto){
        group.setName(dto.getName());

        return groupRepository.save(group);
    }

    //TODO: update user table to remove groups
    @DeleteMapping("/{group}")
    public boolean delete(@PathVariable Group group) {
        usersByGroupRepository.findById(group.getId())
            .ifPresent(usersByGroupRepository::delete);

        usersByRoleRepository.findById(group.getPrimaryRole())
                .ifPresent(usersByRoleRepository::delete);

        groupRepository.delete(group);

        return true;
    }
}
