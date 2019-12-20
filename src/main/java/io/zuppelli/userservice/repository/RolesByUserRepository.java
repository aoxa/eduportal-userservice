package io.zuppelli.userservice.repository;

import io.zuppelli.userservice.model.RolesByUser;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface RolesByUserRepository extends CassandraRepository<RolesByUser, UUID> {
}
