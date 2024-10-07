package me.playgamesgo.petworldbackend.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Request payload for forgot password functionality.
 */
@Getter
@Setter
public final class ForgotPasswordRequest {
    @NotBlank
    @Email
    private String email;
}