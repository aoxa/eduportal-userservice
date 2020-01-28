package io.zuppelli.userservice.model;

import com.datastax.driver.core.DataType;
import io.zuppelli.userservice.annotation.GenerateUUID;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("roles")
@GenerateUUID
public class Role {
    @PrimaryKey
    private UUID id;

    @Column
    private String name;

    @CassandraType(type = DataType.Name.TEXT)
    private Type type = Type.group;

    public enum Type {node, group}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
