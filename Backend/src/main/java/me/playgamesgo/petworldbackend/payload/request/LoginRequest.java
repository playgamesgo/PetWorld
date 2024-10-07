package me.playgamesgo.petworldbackend.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Request payload for login functionality.
 */
@Getter
@Setter
public final class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
