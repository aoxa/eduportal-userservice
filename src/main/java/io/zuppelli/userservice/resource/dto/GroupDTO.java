package io.zuppelli.userservice.resource.dto;

import javax.validation.constraints.NotBlank;

public class GroupDTO {
    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
