package io.zuppelli.userservice.model;

import io.zuppelli.userservice.annotation.GenerateUUID;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("groups")
@GenerateUUID
public class Group {
    @PrimaryKey
    private UUID id;

    @Column
    private String name;

    @Column("primary_role")
    private UUID primaryRole;

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

    public UUID getPrimaryRole() {
        return primaryRole;
    }

    public void setPrimaryRole(UUID primaryRole) {
        this.primaryRole = primaryRole;
    }
}
