package io.zuppelli.userservice.resource;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import io.zuppelli.userservice.UserServiceApplication;
import io.zuppelli.userservice.helper.DataHelper;
import io.zuppelli.userservice.model.Group;
import io.zuppelli.userservice.model.User;
import io.zuppelli.userservice.service.GroupService;
import io.zuppelli.userservice.service.UserService;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.CQLDataLoader;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = UserServiceApplication.class)
@AutoConfigureMockMvc
public class UserResourceTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private GroupService groupService;

    @BeforeClass
    public static void startCassandraEmbedded() throws InterruptedException, TTransportException, ConfigurationException, IOException {
        DataHelper.initialize();
    }

    @AfterClass
    public static void stopCassandraEmbedded() {
        DataHelper.tearDown();
    }

    @Test
    public void testAddUser() throws Exception {
        UUID userId = UUID.randomUUID();
        given(userService.persist(isA(User.class))).willAnswer((a)-> {
            User user = (User) a.getArguments()[0];
            user.setId(userId);
            return user;
        });

        mvc.perform(post("/users")
            .content("{\"firstName\":\"name\", \"lastName\":\"last\", \"email\":\"test@test.com\"}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("name")))
            .andExpect(jsonPath("$.id", is(userId.toString())));

    }

    @Test
    public void testGetUserByEmail() throws Exception {
        UUID userId = UUID.randomUUID();
        String email = "test@test.com";

        given(userService.find(eq(email))).willAnswer((a)->{
            User user = new User();
            user.setId(userId);
            user.setEmail(a.getArguments()[0].toString());
            user.setFirstName("name");
            user.setLastName("last");

            return Optional.of(user);
        });

        mvc.perform(get("/users/email/"+email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("name")))
                .andExpect(jsonPath("$.email", is(email)))
                .andExpect(jsonPath("$.id", is(userId.toString())));;
    }

    @Test
    public void testGetUserById() throws Exception {
        final UUID userId = UUID.randomUUID();
        final String email = "test@test.com";

        prepareUserService(userId, email);

        mvc.perform(get("/users/"+userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("name")))
                .andExpect(jsonPath("$.email", is(email)))
                .andExpect(jsonPath("$.id", is(userId.toString())));;
    }

    @Test
    public void testAddUserGroupNoUserAndNoGroup() throws Exception {
        final UUID userId = UUID.randomUUID();
        final UUID groupId = UUID.randomUUID();

        given(groupService.find(groupId)).willReturn(Optional.empty());

        given(userService.find(eq(userId))).willReturn(Optional.empty());

        mvc.perform(post("/users/"+userId.toString()+"/groups/"+groupId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddUserGroupNoGroup() throws Exception {
        final UUID userId = UUID.randomUUID();
        final UUID groupId = UUID.randomUUID();

        given(groupService.find(groupId)).willReturn(Optional.empty());

        prepareUserService(userId, "test@test.com");

        mvc.perform(post("/users/"+userId.toString()+"/groups/"+groupId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddUserGroupNoUser() throws Exception {
        final UUID userId = UUID.randomUUID();
        final UUID groupId = UUID.randomUUID();

        prepareGroupService(groupId);

        given(userService.find(userId)).willReturn(Optional.empty());

        mvc.perform(post("/users/"+userId.toString()+"/groups/"+groupId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddUserGroup() throws Exception {
        final UUID userId = UUID.randomUUID();
        final UUID groupId = UUID.randomUUID();

        Group group = prepareGroupService(groupId);

        prepareUserService(userId);

        doNothing().when(groupService).addGroup(isA(User.class), isA(Group.class));

        given(groupService.findGroups(isA(User.class))).willReturn(Arrays.asList(group));

        mvc.perform(post("/users/"+userId.toString()+"/groups/"+groupId))
                .andExpect(status().isOk());

    }

    private void prepareUserService(UUID userId) {
        prepareUserService(userId, "test@test.com");
    }

    private void prepareUserService(UUID userId, String email) {
        given(userService.find(eq(userId))).willAnswer((a)->{
            User user = new User();
            user.setId((UUID)a.getArguments()[0]);
            user.setEmail(email);
            user.setFirstName("name");
            user.setLastName("last");

            return Optional.of(user);
        });
    }

    private Group prepareGroupService(UUID groupId) {
        final Group group = new Group();
        group.setName("test group");
        group.setId(groupId);

        given(groupService.find(eq(groupId))).willAnswer((a)->Optional.of(group));

        return group;
    }


}