package io.zuppelli.userservice.resource.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class InviteDTO {
    @NotNull
    @Email
    private String email;

    @NotNull
    @NotEmpty
    private List<UUID> userGroups;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<UUID> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(List<UUID> userGroups) {
        this.userGroups = userGroups;
    }
}
