package me.playgamesgo.petworldbackend.payload.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Embedded;

@Setter
@Getter
public final class SignupRequest {
    @NotBlank
    @Size(min = 4, max = 16)
    private String login;

    @NotBlank
    @Size(max = 64)
    private String name;

    @NotBlank
    @Size(max = 64)
    private String surname;

    @NotBlank
    @Size(max = 64)
    @Email
    private String email;

    @Embedded.Nullable
    @Size(max = 16)
    private String phoneNumber = null;

    @NotBlank
    private String location;

    @NotBlank
    @Size(min = 8, max = 64)
    private String password;

}
