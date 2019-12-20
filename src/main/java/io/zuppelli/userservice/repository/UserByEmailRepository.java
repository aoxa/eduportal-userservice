package io.zuppelli.userservice.repository;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import io.zuppelli.userservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserByEmailRepository {
    @Autowired
    private CassandraTemplate cassandraTemplate;


    public Optional<User> getByEmail(String email) {
        Select select = QueryBuilder.select().from("USERBYEMAIL");
        select.where(QueryBuilder.eq("email", email));
        return Optional.ofNullable(cassandraTemplate.selectOne(select, User.class));
    }
}
