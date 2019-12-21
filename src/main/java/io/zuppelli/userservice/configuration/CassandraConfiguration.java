package io.zuppelli.userservice.configuration;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.datastax.driver.core.schemabuilder.SchemaBuilder.createKeyspace;
import static com.datastax.driver.mapping.NamingConventions.LOWER_CAMEL_CASE;
import static com.datastax.driver.mapping.NamingConventions.LOWER_SNAKE_CASE;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;
import static org.apache.commons.lang3.StringUtils.split;

@Configuration
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

    private void setupKeyspace(Session session, String keyspace) {
        final Map<String, Object> replication = new HashMap<>();
        replication.put("class", "SimpleStrategy");
        replication.put("replication_factor", 1);
        session.execute(createKeyspace(keyspace).ifNotExists().with().replication(replication));
        session.execute("USE " + keyspace);

    }

    @Bean
    public MappingManager mappingManager(Session session) {
        final PropertyMapper propertyMapper =
                new DefaultPropertyMapper()
                        .setNamingStrategy(new DefaultNamingStrategy(LOWER_CAMEL_CASE, LOWER_SNAKE_CASE));

        return new MappingManager(session,
                MappingConfiguration.builder().withPropertyMapper(propertyMapper).build());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() throws IOException {
        String[] statements = split(IOUtils.toString(getClass().getResourceAsStream("/db.cql")), ";");
        Arrays.stream(statements).map(statement -> normalizeSpace(statement) + ";").forEach(session::execute);
    }
}
