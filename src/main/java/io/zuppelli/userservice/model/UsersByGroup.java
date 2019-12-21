package io.zuppelli.userservice.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Table
public class UsersByGroup {
    @PrimaryKey("group_id")
    private UUID groupId;

    @Column("user_ids")
    private Set<UUID> userIds = new HashSet<>();

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public Set<UUID> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<UUID> userId) {
        this.userIds = userIds;
    }
}
