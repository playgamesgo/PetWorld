package me.playgamesgo.petworldbackend.payload.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public final class SignupRequest {
    @NotBlank
    @Size(min = 4, max = 16)
    private String username;

    @NotBlank
    @Size(max = 64)
    private String firstName;

    @NotBlank
    @Size(max = 64)
    private String lastName;

    @NotBlank
    @Size(max = 64)
    @Email
    private String email;

    @NotBlank
    private String location;

    @NotBlank
    @Size(min = 8, max = 64)
    private String password;

}
