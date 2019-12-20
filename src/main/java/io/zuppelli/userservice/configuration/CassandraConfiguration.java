package io.zuppelli.userservice.configuration;

import com.datastax.driver.core.Session;
import io.zuppelli.userservice.model.User;
import io.zuppelli.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

@Configuration
@PropertySource("classpath:cassandra.properties")
@EnableCassandraRepositories(basePackages = "io.zuppelli.userservice.repository")
public class CassandraConfiguration extends AbstractCassandraConfiguration {
    @Autowired
    private Session session;

    private Environment environment;

    @Autowired
    public CassandraConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Value("${cassandra.cluster}")
    private String cassandraCluster;

    @Value("${cassandra.port}")
    private Integer cassandraPort;

    @Value("${cassandra.keyspace}")
    private String cassandraKeyspace;

    @Value("${server.port}")
    private String serverport;

    @Override
    protected String getKeyspaceName() {
        return environment.getProperty("cassandra.keyspace");
    }

    @Bean
    public CassandraClusterFactoryBean cluster() {

        CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean();
        cluster.setContactPoints(environment.getProperty("cassandra.cluster"));
        cluster.setPort(environment.getProperty("cassandra.port", Integer.class));
        return cluster;
    }

    @Bean
    public CassandraSessionFactoryBean session(CassandraConverter converter) throws Exception {
        CassandraSessionFactoryBean session = new CassandraSessionFactoryBean();
        session.setCluster(cluster().getObject());
        session.setKeyspaceName(this.getKeyspaceName());
        session.setConverter(converter);
        session.setSchemaAction(SchemaAction.CREATE_IF_NOT_EXISTS);
        return session;
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Bean
    public CassandraMappingContext cassandraMapping() {
        return new BasicCassandraMappingContext();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {

        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ROLES")
                .append("( id uuid PRIMARY KEY,")
                .append("name text);");

        session.execute(sb.toString());

        sb = new StringBuilder("CREATE TABLE IF NOT EXISTS GROUPS")
                .append("( id uuid PRIMARY KEY,")
                .append("name text,")
                .append("primaryRole uuid);");

        session.execute(sb.toString());

        sb = new StringBuilder("CREATE TABLE IF NOT EXISTS USERS")
                .append("( id uuid PRIMARY KEY,")
                .append("firstName text,")
                .append("lastName text,")
                .append("email text,")
                .append("password text,")
                .append("children list<uuid>);");

        session.execute(sb.toString());

        sb = new StringBuilder("CREATE TABLE IF NOT EXISTS GROUPS")
                .append("( id uuid PRIMARY KEY,")
                .append("name text,")
                .append("primaryRole uuid);");

        session.execute(sb.toString());

        sb = new StringBuilder("CREATE TABLE IF NOT EXISTS GROUPBYROLE")
                .append("( roleid uuid PRIMARY KEY,")
                .append("groupid uuid);");

        session.execute(sb.toString());

        sb = new StringBuilder("CREATE TABLE IF NOT EXISTS USERBYROLE")
                .append("( roleid uuid PRIMARY KEY,")
                .append("userid uuid);");

        session.execute(sb.toString());

        sb = new StringBuilder("CREATE MATERIALIZED VIEW IF NOT EXISTS USERBYEMAIL ")
                .append("AS SElECT * ")
                .append("from USERS ")
                .append("WHERE id is not null ")
                .append(" AND email is not null ")
                .append("PRIMARY KEY(email, id);");

        session.execute(sb.toString());

        sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ROLESBYUSER")
                .append("( userid uuid PRIMARY KEY,")
                .append("roleids set<uuid>);");

        session.execute(sb.toString());

        sb = new StringBuilder("CREATE TABLE IF NOT EXISTS GROUPSBYUSER")
                .append("( userid uuid PRIMARY KEY,")
                .append("groupids set<uuid>);");

        session.execute(sb.toString());

        sb = new StringBuilder("CREATE TABLE IF NOT EXISTS USERSBYGROUP")
                .append("( groupid uuid PRIMARY KEY,")
                .append("userids list<uuid>);");

        session.execute(sb.toString());

        /*
        for(int i=1; i<6;i++) {
            Rating rating = new Rating("Rocky " + i, i);
            ratingRepository.save(rating);
        }
        */
    }
}
