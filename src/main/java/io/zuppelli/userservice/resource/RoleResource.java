package io.zuppelli.userservice.resource;

import io.zuppelli.userservice.model.Role;
import io.zuppelli.userservice.repository.GroupsByRoleRepository;
import io.zuppelli.userservice.repository.RoleRepository;
import io.zuppelli.userservice.repository.UsersByRoleRepository;
import io.zuppelli.userservice.resource.dto.RoleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/roles")
public class RoleResource
{
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GroupsByRoleRepository groupsByRoleRepository;

    @Autowired
    private UsersByRoleRepository usersByRoleRepository;

    @PostMapping
    public Role addRole(@RequestBody RoleDTO dto) {
        Role role = new Role();
        role.setName(dto.getName().replace(" ", "_"));

        return roleRepository.save(role);
    }

    @GetMapping("/{role}")
    public Role getRole(@PathVariable Role role) {
        return role;
    }


    @PutMapping("/{role}")
    public Role modifyRole(@PathVariable Role role, @RequestBody RoleDTO dto) {
        role.setName(dto.getName());

        return roleRepository.save(role);
    }

    @DeleteMapping("/{role}")
    public boolean deleteRole(@PathVariable Role role) {
        if(groupsByRoleRepository.findById(role.getId()).isPresent()) {
            return false;
        }

        usersByRoleRepository.findById(role.getId())
                .ifPresent(usersByRoleRepository::delete);

        roleRepository.delete(role);
        return true;
    }
}
