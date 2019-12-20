package io.zuppelli.userservice.repository;

import io.zuppelli.userservice.model.Role;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface RoleRepository extends CassandraRepository<Role, UUID> {
}
