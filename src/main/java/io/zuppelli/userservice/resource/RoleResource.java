package io.zuppelli.userservice.resource;

import io.zuppelli.userservice.model.Role;
import io.zuppelli.userservice.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/roles")
public class RoleResource
{
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/{role}")
    public Role getRole(@PathVariable Role role) {
        return role;
    }

    @PostMapping
    public Role addRole() {
        Role role = new Role();
        role.setName("test");

        return roleRepository.save(role);
    }
}
