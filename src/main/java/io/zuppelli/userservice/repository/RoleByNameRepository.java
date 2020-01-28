package io.zuppelli.userservice.repository;

import io.zuppelli.userservice.model.RoleByName;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface RoleByNameRepository extends CassandraRepository<RoleByName, String> {
}
