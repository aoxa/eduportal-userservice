package io.zuppelli.userservice.resource;

import com.datastax.driver.core.PagingState;
import io.zuppelli.userservice.exception.BadRequestException;
import io.zuppelli.userservice.exception.EntityNotFoundException;
import io.zuppelli.userservice.model.*;
import io.zuppelli.userservice.repository.*;
import io.zuppelli.userservice.resource.dto.AuthDTO;
import io.zuppelli.userservice.resource.dto.InviteDTO;
import io.zuppelli.userservice.resource.dto.UserDTO;
import io.zuppelli.userservice.service.GroupService;
import io.zuppelli.userservice.service.RoleService;
import io.zuppelli.userservice.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserResource {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserByUsernameRepository usernameRepository;

    @PostMapping
    public User addUser(@RequestBody UserDTO dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());

        return userService.persist(user);
    }

    @GetMapping("/{id}")
    public Optional<User> getUser(@PathVariable UUID id) {
        return userService.find(id);
    }

    @PutMapping("/{user}")
    public User editUser(@RequestBody @Valid UserDTO dto, @PathVariable User user) {
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());

        if(!StringUtils.isBlank(dto.getUsername())) user.setUsername(dto.getUsername());

        return userService.persist(user);
    }

    @GetMapping
    public Page<User> list(String hash ) {
        Pageable pageRequest = CassandraPageRequest.of(0,5);

        if(null != hash) {
            PagingState pagingState = PagingState.fromBytes(Base64.decode(hash));
            pageRequest = CassandraPageRequest.of(pageRequest, pagingState).next();
        }

        Slice<User> slice = userService.find(pageRequest);
        Page<User> page = new Page();
        page.setElements(slice.getContent());
        page.setNext(slice.hasNext());
        page.setPageHash((CassandraPageRequest) slice.getPageable(), hash);

        return page;
    }

    @PostMapping("/{user}/activate")
    public Boolean activateUser(@PathVariable User user) {
        user.setEnabled(!user.isEnabled());
        userService.persist(user);

        return user.isEnabled();
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("/{user}/groups/{group}")
    public void removeGroup(@PathVariable UUID user, @PathVariable UUID group) {
        User u = userService.find(user).orElseThrow(EntityNotFoundException::new);

        Group g = groupService.find(group).orElseThrow(EntityNotFoundException::new);

        groupService.removeGroup(u,g);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{user}/groups/{group}")
    public List<Group> addGroup(@PathVariable UUID user, @PathVariable UUID group) {
        User u = userService.find(user).orElseThrow(EntityNotFoundException::new);

        Group g = groupService.find(group).orElseThrow(EntityNotFoundException::new);

        groupService.addGroup(u, g);

        return groupService.findGroups(u);
    }

    @GetMapping("/{user}/groups")
    public List<Group> getUserGroups(@PathVariable User user) {
        if (null == user) throw new EntityNotFoundException();

        return groupService.findGroups(user);
    }

    @GetMapping("/{user}/roles")
    public List<Role> getUserRoles(@PathVariable User user) {
        if (null == user) throw new EntityNotFoundException();

        return roleService.getRoles(user);
    }

    @PostMapping("/{user}/roles/{role}")
    public List<Role> addRole(@PathVariable UUID user, @PathVariable UUID role) {
        User u = userService.find(user).orElseThrow(EntityNotFoundException::new);
        Role r = roleService.find(role).orElseThrow(EntityNotFoundException::new);

        roleService.addRole(u, r);

        return roleService.getRoles(u);
    }

    @PostMapping("/invite")
    public User invite(@Valid @RequestBody InviteDTO dto) {
        final User user = new User();
        user.setUsername(dto.getEmail());
        user.setEmail(dto.getEmail());

        userService.persist(user);

        dto.getUserGroups()
                .forEach(uuid->groupService.find(uuid)
                        .ifPresent(group -> groupService.addGroup(user, group)));
        return user;
    }

    @GetMapping("/email/{email}")
    public Optional<User> getUser(@PathVariable String email) {
        return userService.find(email);
    }

    @GetMapping("/username/{username}")
    public Optional<User> getByUsername(@PathVariable String username) {
        UserByUsername userByUsername = usernameRepository.findById(username)
                .orElseThrow(EntityNotFoundException::new);

        return userService.find(userByUsername.getUserId());
    }

    @GetMapping("/username/query/{username}")
    public List<Optional<User>> getLikeUsername(@PathVariable String username) {
        return usernameRepository.findAllByUsernameGreaterThanEqualAndUsernameLessThanEqual(username, username+"z")
                .stream().map(UserByUsername::getUserId).map(userService::find).collect(Collectors.toList());
    }
}