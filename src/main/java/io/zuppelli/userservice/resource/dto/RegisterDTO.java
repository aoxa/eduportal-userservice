package io.zuppelli.userservice.resource.dto;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

public class RegisterDTO {
    @NotEmpty
    private String password;
    @NotEmpty
    private String retypePassword;
    @NotEmpty
    private String username;

    private List<UserDTO> children = new ArrayList<>();

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRetypePassword() {
        return retypePassword;
    }

    public void setRetypePassword(String retypePassword) {
        this.retypePassword = retypePassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<UserDTO> getChildren() {
        return children;
    }

    public void setChildren(List<UserDTO> children) {
        this.children = children;
    }
}
