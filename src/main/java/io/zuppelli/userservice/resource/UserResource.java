package io.zuppelli.userservice.resource;

import io.zuppelli.userservice.model.*;
import io.zuppelli.userservice.repository.*;
import io.zuppelli.userservice.service.GroupService;
import io.zuppelli.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private RolesByUserRepository rolesByUserRepository;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UsersByGroupRepository usersByGroup;

    @PostMapping()
    public User addUser() {

        User user = new User();
        user.setEmail("pedroz@email.com");
        user.setFirstName("Pedro");
        user.setLastName("Zuppelli");

        return userService.persist(user);
    }

    @GetMapping("/email/{email}")
    public Optional<User> getUser(@PathVariable String email) {
        return userService.findUser(email);
    }

    @GetMapping("/{id}")
    public Optional<User> getUser(@PathVariable UUID id) {
        return userService.findUser(id);
    }

    @PostMapping("/{user}/groups/{group}")
    public List<Group> addGroup(@PathVariable User user, @PathVariable Group group) {
        groupService.addGroup(user, group);

        return groupService.findGroups(user);
    }

    @GetMapping("/{user}/groups")
    public List<Group> getUserGroups(@PathVariable User user) {
        return groupService.findGroups(user);
    }

    @GetMapping("/{id}/roles")
    public Optional<RolesByUser> getUserRoles(@PathVariable UUID id) {
        return rolesByUserRepository.findById(id);
    }

    @PostMapping("/{user}/roles/{role}")
    public User addRole(@PathVariable User user, @PathVariable Role role) {
        Optional<RolesByUser> rolesByUser = rolesByUserRepository.findById(user.getId());

        RolesByUser rbu = rolesByUser.orElseGet(()->{
            RolesByUser r = new RolesByUser();
            r.setUserId(user.getId());
            return r;
        });
        rbu.getRoleIds().add(role.getId());
        rolesByUserRepository.save(rbu);

        return user;
    }
}
