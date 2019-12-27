package io.zuppelli.userservice.model;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.zuppelli.userservice.annotation.GenerateUUID;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;
import java.util.UUID;

@Table("users")
@GenerateUUID
public class User {
    @PrimaryKey
    @PartitionKey
    private UUID id;

    @Column
    private String username;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    @ClusteringColumn(1)
    private String lastName;

    @Column
    @ClusteringColumn(2)
    private UUID parent;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private List<UUID> children;

    @Column
    private boolean enabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<UUID> getChildren() {
        return children;
    }

    public void setChildren(List<UUID> children) {
        this.children = children;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UUID getParent() {
        return parent;
    }

    public void setParent(UUID parent) {
        this.parent = parent;
    }
}
