package io.zuppelli.userservice.repository;

import io.zuppelli.userservice.model.UsersByRole;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface UsersByRoleRepository extends CassandraRepository<UsersByRole, UUID> {
}
