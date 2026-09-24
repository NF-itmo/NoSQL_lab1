package org.controllers.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AuthRequest {
    @NotBlank(message = "Login should be specified")
    private String login;

    @NotBlank(message = "Password should be specified")
    private String password;
}