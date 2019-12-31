package io.zuppelli.userservice.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("group_by_role")
public class GroupByRole {
    @PrimaryKey("role_id")
    private UUID roleId;

    @Column("group_id")
    private UUID groupId;

    public GroupByRole(){}

    public GroupByRole(UUID roleId, UUID groupId) {
        this.roleId = roleId;
        this.groupId = groupId;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }
}
