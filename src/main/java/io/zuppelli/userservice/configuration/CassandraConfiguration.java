package io.zuppelli.userservice.configuration;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import io.zuppelli.userservice.model.User;
import io.zuppelli.userservice.repository.UserRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.datastax.driver.core.schemabuilder.SchemaBuilder.createKeyspace;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;
import static org.apache.commons.lang3.StringUtils.split;

@Configuration
@EnableAutoConfiguration
@PropertySource("classpath:cassandra.properties")
@EnableCassandraRepositories(basePackages = "io.zuppelli.userservice.repository")
public class CassandraConfiguration {
    @Autowired
    private Session session;

    private Environment environment;

    @Autowired
    public CassandraConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public Cluster cluster() {
        return Cluster.builder().addContactPoint(environment.getProperty("cassandra.cluster"))
                .withPort(environment.getProperty("cassandra.port", Integer.class)).build();
    }

    @Bean
    public Session session(Cluster cluster, @Value("${cassandra.keyspace}") String keyspace)
            throws IOException {
        final Session session = cluster.connect();
        setupKeyspace(session, keyspace);
        return session;
    }

    private void setupKeyspace(Session session, String keyspace) throws IOException {
        final Map<String, Object> replication = new HashMap<>();
        replication.put("class", "SimpleStrategy");
        replication.put("replication_factor", 1);
        session.execute(createKeyspace(keyspace).ifNotExists().with().replication(replication));
        session.execute("USE " + keyspace);
        //    String[] statements = split(IOUtils.toString(getClass().getResourceAsStream("/cql/setup.cql")), ";");
        //    Arrays.stream(statements).map(statement -> normalizeSpace(statement) + ";").forEach(session::execute);
    }

    @Bean
    public CassandraMappingContext cassandraMapping() {
        return new BasicCassandraMappingContext();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() throws IOException {
        String[] statements = split(IOUtils.toString(getClass().getResourceAsStream("/db.cql")), ";");
        Arrays.stream(statements).map(statement -> normalizeSpace(statement) + ";").forEach(session::execute);
    }
}
