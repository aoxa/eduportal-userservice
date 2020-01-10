package io.zuppelli.userservice.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.*;

@Table("groups_by_user")
public class GroupsByUser {
    @PrimaryKey("user_id")
    private UUID userId;

    @Column("group_ids")
    private Set<UUID> groupIds = new HashSet<>();

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Set<UUID> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(Set<UUID> groupIds) {
        this.groupIds = groupIds;
    }
}
