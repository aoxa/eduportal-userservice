package io.zuppelli.userservice.repository;

import io.zuppelli.userservice.model.Group;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface GroupRepository extends CassandraRepository<Group, UUID> {
}
