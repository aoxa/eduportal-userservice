package io.zuppelli.userservice.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.*;

@Table
public class GroupsByUser {
    @PrimaryKey
    private UUID userId;
    @Column
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
