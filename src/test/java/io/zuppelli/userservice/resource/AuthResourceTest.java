package io.zuppelli.userservice.resource;

import io.zuppelli.userservice.UserServiceApplication;
import io.zuppelli.userservice.helper.DataHelper;
import io.zuppelli.userservice.model.User;
import io.zuppelli.userservice.model.UserByUsername;
import io.zuppelli.userservice.repository.UserByUsernameRepository;
import io.zuppelli.userservice.service.UserService;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = UserServiceApplication.class)
@AutoConfigureMockMvc
public class AuthResourceTest {
    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserService userService;

    @MockBean
    private UserByUsernameRepository usernameRepository;

    @Autowired
    private MockMvc mvc;

    @BeforeClass
    public static void startCassandraEmbedded() throws InterruptedException, TTransportException, ConfigurationException, IOException {
        DataHelper.initialize();
    }

    @AfterClass
    public static void stopCassandraEmbedded() {
        DataHelper.tearDown();
    }

    @Test
    public void auth() throws Exception {
        String username = "username";

        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setFirstName("name");
        user.setId(userId);
        user.setPassword("pass");

        UserByUsername ubu = new UserByUsername();
        ubu.setUserId(userId);
        ubu.setUsername(username);

        given(passwordEncoder.matches("mypass", "pass")).willReturn(true);

        given(usernameRepository.findById(username)).willReturn(Optional.of(ubu));

        given(userService.find(userId)).willReturn(Optional.of(user));

        mvc.perform(post("/auth")
                .content(String.format("{\"username\":\"%s\", \"password\":\"mypass\"}", username))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("name")))
                .andExpect(jsonPath("$.id", is(userId.toString())));
    }

    @Test
    public void authByEmail() throws Exception {
        String email = "test@test.com";

        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setFirstName("name");
        user.setId(userId);
        user.setPassword("pass");
        user.setEmail(email);

        given(passwordEncoder.matches("mypass", "pass")).willReturn(true);

        given(userService.find(email)).willReturn(Optional.of(user));

        mvc.perform(post("/auth")
                .content(String.format("{\"username\":\"%s\", \"password\":\"mypass\"}", email))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("name")))
                .andExpect(jsonPath("$.id", is(userId.toString())));
    }

    @Test
    public void authByEmailPasswordMismatch() throws Exception {
        String email = "test@test.com";

        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setFirstName("name");
        user.setId(userId);
        user.setPassword("pass");
        user.setEmail(email);

        given(passwordEncoder.matches("mypass", "pass")).willReturn(false);

        given(userService.find(email)).willReturn(Optional.of(user));

        mvc.perform(post("/auth")
                .content(String.format("{\"username\":\"%s\", \"password\":\"mypass\"}", email))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void authByEmailNoUserFound() throws Exception {
        String email = "test@test.com";

        given(userService.find(email)).willReturn(Optional.empty());

        mvc.perform(post("/auth")
                .content(String.format("{\"username\":\"%s\", \"password\":\"mypass\"}", email))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void authPasswordMismatch() throws Exception {
        String username = "username";

        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setFirstName("name");
        user.setId(userId);
        user.setPassword("pass");

        UserByUsername ubu = new UserByUsername();
        ubu.setUserId(userId);
        ubu.setUsername(username);

        given(passwordEncoder.matches("mypass", "pass")).willReturn(false);

        given(usernameRepository.findById(username)).willReturn(Optional.of(ubu));

        given(userService.find(userId)).willReturn(Optional.of(user));

        mvc.perform(post("/auth")
                .content(String.format("{\"username\":\"%s\", \"password\":\"mypass\"}", username))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void authNoUserPasswordMismatch() throws Exception {
        String username = "username";

        given(passwordEncoder.matches("mypass", "pass")).willReturn(false);

        given(usernameRepository.findById(username)).willReturn(Optional.empty());

        mvc.perform(post("/auth")
                .content(String.format("{\"username\":\"%s\", \"password\":\"mypass\"}", username))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void authUpdateUserByEmail() throws Exception {
        String username = "test@test.com";
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setFirstName("test");
        user.setId(userId);

        given(passwordEncoder.encode("mypass")).willReturn("encoded");

        given(userService.persist(user)).willAnswer((a)->user);

        given(userService.find(username)).willReturn(Optional.of(user));

        mvc.perform(post("/auth/update")
                .content(String.format("{\"username\":\"%s\", \"password\":\"mypass\"}", username))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void authUpdateUserByUsername() throws Exception {
        String username = "test";
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setFirstName("test");
        user.setId(userId);

        UserByUsername ubu = new UserByUsername();
        ubu.setUsername("test");
        ubu.setUserId(userId);

        given(passwordEncoder.encode("mypass")).willReturn("encoded");

        given(userService.persist(user)).willAnswer((a)->user);

        given(usernameRepository.findById(username)).willReturn(Optional.of(ubu));

        given(userService.find(userId)).willReturn(Optional.of(user));

        mvc.perform(post("/auth/update")
                .content(String.format("{\"username\":\"%s\", \"password\":\"mypass\"}", username))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void authUpdateUserNotFound() throws Exception {
        String username = "test@test.com";

        given(userService.find(username)).willReturn(Optional.empty());

        mvc.perform(post("/auth/update")
                .content(String.format("{\"username\":\"%s\", \"password\":\"mypass\"}", username))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void authUpdateUsernameNotFound() throws Exception {
        String username = "test";

        given(usernameRepository.findById(username)).willReturn(Optional.empty());

        mvc.perform(post("/auth/update")
                .content(String.format("{\"username\":\"%s\", \"password\":\"mypass\"}", username))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registerEmailMismatch() throws Exception {
        String username = "test";

        mvc.perform(post("/auth/test@test.com/register")
                .content(String.format("{\"username\":\"%s\", \"password\":\"apass\", \"retypePassword\":\"mypass\"}", username))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registerUserNotFound() throws Exception {
        String username = "test@test.com";

        given(userService.find(username)).willReturn(Optional.empty());

        mvc.perform(post(String.format("/auth/%s/register", username))
                .content(String.format("{\"username\":\"test\", \"password\":\"mypass\", \"retypePassword\":\"mypass\"}", username))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void register() throws Exception {
        String username = "test@test.com";

        User user = new User();
        user.setEmail(username);

        given(userService.find(username)).willReturn(Optional.of(user));

        mvc.perform(post(String.format("/auth/%s/register", username))
                .content(String.format("{\"username\":\"test\", \"password\":\"mypass\", \"retypePassword\":\"mypass\"}", username))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}