package io.zuppelli.userservice.repository;

import io.zuppelli.userservice.model.UserByUsername;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface UserByUsernameRepository extends CassandraRepository<UserByUsername, String> {
}
