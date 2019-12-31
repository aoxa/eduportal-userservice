package io.zuppelli.userservice.resource;

import io.zuppelli.userservice.exception.EntityNotFoundException;
import io.zuppelli.userservice.model.Group;
import io.zuppelli.userservice.model.Role;
import io.zuppelli.userservice.repository.GroupRepository;
import io.zuppelli.userservice.repository.RoleRepository;
import io.zuppelli.userservice.repository.UsersByGroupRepository;
import io.zuppelli.userservice.repository.UsersByRoleRepository;
import io.zuppelli.userservice.resource.dto.GroupDTO;
import io.zuppelli.userservice.service.GroupService;
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
    private GroupService groupService;

    @Autowired
    private UsersByGroupRepository usersByGroupRepository;

    @Autowired
    private UsersByRoleRepository usersByRoleRepository;

    @PostMapping
    public Group add(@RequestBody GroupDTO dto) {
        return groupService.builder().add("name", dto.getName()).build();
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

    @DeleteMapping("/{group}")
    public boolean delete(@PathVariable Group group) {
        if(null == group) {
            throw new EntityNotFoundException();
        }

        usersByGroupRepository.findById(group.getId())
            .ifPresent(usersByGroupRepository::delete);

        usersByRoleRepository.findById(group.getPrimaryRole())
                .ifPresent(usersByRoleRepository::delete);

        groupRepository.delete(group);

        return true;
    }
}
