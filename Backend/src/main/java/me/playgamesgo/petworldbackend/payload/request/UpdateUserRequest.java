package me.playgamesgo.petworldbackend.payload.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Embedded;

/**
 * Request payload for updating an advertisement.
 */
@Getter
@Setter
public final class UpdateUserRequest {
    @Embedded.Nullable
    private String firstName;

    @Embedded.Nullable
    private String lastName;

    @Embedded.Nullable
    @Email
    private String email;

    @Embedded.Nullable
    private String phoneNumber;

    @Embedded.Nullable
    private String location;

    @Embedded.Nullable
    private String password;
}
