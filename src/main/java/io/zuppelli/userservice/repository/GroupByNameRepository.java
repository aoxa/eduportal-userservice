package io.zuppelli.userservice.repository;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import io.zuppelli.userservice.model.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class GroupByNameRepository {
    @Autowired
    private CassandraTemplate cassandraTemplate;

    public Optional<Group> getByName(String name) {
        Select select = QueryBuilder.select().from("GROUP_BY_NAME");
        select.where(QueryBuilder.eq("name", name));
        return Optional.ofNullable(cassandraTemplate.selectOne(select, Group.class));
    }
}
