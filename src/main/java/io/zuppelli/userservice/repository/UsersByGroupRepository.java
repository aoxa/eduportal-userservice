package io.zuppelli.userservice.repository;

import io.zuppelli.userservice.model.UserByGroup;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface UsersByGroupRepository extends CassandraRepository<UserByGroup, UUID> {
}
