package io.zuppelli.userservice.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.*;

@Table("roles_by_user")
public class RolesByUser {

    @PrimaryKey("user_id")
    private UUID userId;

    @Column("role_ids")
    private Set<UUID> roleIds = new HashSet<>();

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Set<UUID> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<UUID> roleIds) {
        this.roleIds = roleIds;
    }
}
