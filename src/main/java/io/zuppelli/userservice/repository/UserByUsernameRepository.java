package io.zuppelli.userservice.repository;

import io.zuppelli.userservice.model.UserByUsername;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;

public interface UserByUsernameRepository extends CassandraRepository<UserByUsername, String> {
    @Query(allowFiltering = true)
    List<UserByUsername> findAllByUsernameGreaterThanEqual(String username);
}
