package io.zuppelli.userservice.repository;

import io.zuppelli.userservice.model.GroupsByUser;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface GroupByUserRepository extends CassandraRepository<GroupsByUser, UUID> {
}
