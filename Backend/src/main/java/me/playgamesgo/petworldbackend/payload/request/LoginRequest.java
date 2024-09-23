package me.playgamesgo.petworldbackend.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
