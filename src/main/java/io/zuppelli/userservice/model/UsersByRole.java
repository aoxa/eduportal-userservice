package io.zuppelli.userservice.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Set;
import java.util.UUID;

@Table("users_by_role")
public class UsersByRole {
    @PrimaryKey("role_id")
    private UUID roleId;

    @Column("user_ids")
    private Set<UUID> userIds;

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

    public Set<UUID> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<UUID> userId) {
        this.userIds = userIds;
    }
}
