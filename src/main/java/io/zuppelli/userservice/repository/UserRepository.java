package io.zuppelli.userservice.repository;

import io.zuppelli.userservice.model.User;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface UserRepository extends CassandraRepository<User, UUID> {
}
