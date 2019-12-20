package io.zuppelli.userservice.repository;

import io.zuppelli.userservice.model.GroupByRole;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface GroupsByRoleRepository extends CassandraRepository<GroupByRole, UUID> {
}
