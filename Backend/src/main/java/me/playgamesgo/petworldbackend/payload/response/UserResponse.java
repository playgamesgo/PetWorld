package me.playgamesgo.petworldbackend.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public final class UserResponse {
    long id;
    String firstName;
    String lastName;
    String email;
    String location;
    Date createdAt;

    public UserResponse(long id, String firstName, String lastName, String email, String location, Date createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.location = location;
        this.createdAt = createdAt;
    }
}
