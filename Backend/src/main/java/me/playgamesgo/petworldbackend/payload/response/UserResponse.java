package me.playgamesgo.petworldbackend.payload.response;

import lombok.Getter;
import lombok.Setter;
import me.playgamesgo.petworldbackend.models.User;

import java.util.Date;

@Getter
@Setter
public final class UserResponse {
    long id;
    String name;
    String surname;
    String email;
    String phoneNumber;
    String location;
    Date createdAt;

    public UserResponse(long id, String firstName, String lastName, String email, String phoneNumber, String location, Date createdAt) {
        this.id = id;
        this.name = firstName;
        this.surname = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.createdAt = createdAt;
    }

    public static UserResponse fromUser(User user) {
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getLocation(), user.getCreatedAt());
    }
}
