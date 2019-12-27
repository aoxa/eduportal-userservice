package io.zuppelli.userservice.resource;

import io.zuppelli.userservice.exception.BadRequestException;
import io.zuppelli.userservice.exception.EntityNotFoundException;
import io.zuppelli.userservice.model.User;
import io.zuppelli.userservice.model.UserByUsername;
import io.zuppelli.userservice.repository.UserByUsernameRepository;
import io.zuppelli.userservice.resource.dto.AuthDTO;
import io.zuppelli.userservice.resource.dto.RegisterDTO;
import io.zuppelli.userservice.resource.dto.UserDTO;
import io.zuppelli.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthResource {
    @Autowired
    private UserService userService;

    @Autowired
    private UserByUsernameRepository usernameRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public User auth(@RequestBody @Valid AuthDTO auth) {
        final String username = auth.getUsername();

        if (username.contains("@")) {
            User user = userService.find(username).orElseThrow(BadRequestException::new);

            if (! passwordEncoder.matches(auth.getPassword(), user.getPassword())) {
                throw new BadRequestException();
            }

            return user;
        }

        UserByUsername userByUsername = usernameRepository.findById(username).orElseThrow(BadRequestException::new);

        User user = userService.find(userByUsername.getUserId()).orElseThrow(BadRequestException::new);

        if (! passwordEncoder.matches(auth.getPassword(), user.getPassword())) {
            throw new BadRequestException();
        }

        return user;
    }

    @PostMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public void authUpdate(@RequestBody @Valid AuthDTO auth) {
        User user = userService.find(auth.getUsername()).orElseThrow(EntityNotFoundException::new);

        user.setPassword(passwordEncoder.encode(auth.getPassword()));

        userService.persist(user);
    }

    @PostMapping("/{email}/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@PathVariable String email, @RequestBody @Valid RegisterDTO register) {
        if(!register.getPassword().equals(register.getRetypePassword())) {
            throw new BadRequestException();
        }

        User user = userService.find(email).orElseThrow(BadRequestException::new);

        user.setUsername(register.getUsername());

        for(UserDTO registerChild : register.getChildren()) {
            User child = new User();
            user.setFirstName(registerChild.getFirstName());
            user.setLastName(user.getLastName());
            user.setEmail(child.getEmail());
            child.setParent(user.getId());

            child = userService.persist(child);

            user.getChildren().add(child.getId());
        }

        userService.persist(user);
    }
}
