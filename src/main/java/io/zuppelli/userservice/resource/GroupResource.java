package io.zuppelli.userservice.resource;

import io.zuppelli.userservice.model.Group;
import io.zuppelli.userservice.model.Role;
import io.zuppelli.userservice.repository.GroupRepository;
import io.zuppelli.userservice.repository.RoleRepository;
import io.zuppelli.userservice.resource.dto.GroupDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/groups")
public class GroupResource {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GroupRepository groupRepository;

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
}
