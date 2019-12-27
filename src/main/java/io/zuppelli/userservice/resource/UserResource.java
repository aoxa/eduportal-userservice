package io.zuppelli.userservice.resource;

import io.zuppelli.userservice.exception.EntityNotFoundException;
import io.zuppelli.userservice.model.*;
import io.zuppelli.userservice.repository.*;
import io.zuppelli.userservice.resource.dto.UserDTO;
import io.zuppelli.userservice.service.GroupService;
import io.zuppelli.userservice.service.RoleService;
import io.zuppelli.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserResource {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private GroupService groupService;

    @PostMapping
    public User addUser(@RequestBody UserDTO dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());

        return userService.persist(user);
    }

    @GetMapping("/email/{email}")
    public Optional<User> getUser(@PathVariable String email) {
        return userService.find(email);
    }

    @GetMapping("/{id}")
    public Optional<User> getUser(@PathVariable UUID id) {
        return userService.find(id);
    }

    @PostMapping("/{user}/groups/{group}")
    public List<Group> addGroup(@PathVariable UUID user, @PathVariable UUID group) {

        User u = userService.find(user).orElseThrow(EntityNotFoundException::new);
        Group g = groupService.find(group).orElseThrow(EntityNotFoundException::new);

        groupService.addGroup(u, g);

        return groupService.findGroups(u);
    }

    @GetMapping("/{user}/groups")
    public List<Group> getUserGroups(@PathVariable User user) {
        if( null == user) throw new EntityNotFoundException();

        return groupService.findGroups(user);
    }

    @GetMapping("/{user}/roles")
    public List<Role> getUserRoles(@PathVariable User user) {
        if( null == user) throw new EntityNotFoundException();

        return roleService.getRoles(user);
    }

    @PostMapping("/{user}/roles/{role}")
    public List<Role> addRole(@PathVariable UUID user, @PathVariable UUID role) {
        User u = userService.find(user).orElseThrow(EntityNotFoundException::new);
        Role r = roleService.find(role).orElseThrow(EntityNotFoundException::new);

        roleService.addRole(u, r);

        return roleService.getRoles(u);
    }
}
