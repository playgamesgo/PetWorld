package me.playgamesgo.petworldbackend.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class ForgotPasswordRequest {
    @NotBlank
    @Email
    private String email;
}