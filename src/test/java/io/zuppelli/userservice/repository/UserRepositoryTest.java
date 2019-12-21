package io.zuppelli.userservice.repository;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import io.zuppelli.userservice.configuration.CassandraConfiguration;
import io.zuppelli.userservice.model.User;
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
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@EnableAutoConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CassandraConfiguration.class)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private final UUID userId = UUID.randomUUID();

    @BeforeClass
    public static void startCassandraEmbedded() throws InterruptedException, TTransportException, ConfigurationException, IOException {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra();
        Cluster cluster = Cluster.builder()
                .addContactPoint("127.0.0.1").withPort(9142).build();
        Session session = cluster.connect();

        final CQLDataLoader cqlDataLoader = new CQLDataLoader(session);
        cqlDataLoader.load(new ClassPathCQLDataSet("db.cql", false, true, "userkeyspace"));
    }

    @Test
    public void test() {
        User user = new User();
        user.setId(userId);
        user.setFirstName("Pepe");
        user.setLastName("Trueno");
        user.setEmail("pepe@trueno.com");

        userRepository.save(user);

        User recovered = userRepository.findById(userId).get();
        assertEquals(userId, recovered.getId());

    }

    @AfterClass
    public static void stopEmbedded() {
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
    }
}