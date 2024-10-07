package me.playgamesgo.petworldbackend.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import me.playgamesgo.petworldbackend.models.User;

import java.util.Date;

/**
 * Response payload for user details.
 */
@Getter
@Setter
public final class UserResponse {
    long id;
    String login;
    String name;
    String surname;
    String email;
    @JsonProperty("phone_number")
    String phoneNumber;
    String location;
    @JsonProperty("created_on")
    Date createdOn;
    @JsonProperty("last_modified_on")
    Date lastModifiedOn;
    @JsonProperty("prefers_phone_call")
    Boolean prefersPhoneCall = false;
    @JsonProperty("prefers_telegram")
    Boolean prefersTelegram = false;
    @JsonProperty("is_active")
    Boolean isActive = true;
    @JsonProperty("is_superuser")
    Boolean isSuperuser = false;


    public UserResponse(long id, String login, String firstName, String lastName, String email, String phoneNumber, String location, Date createdAt) {
        this.id = id;
        this.login = login;
        this.name = firstName;
        this.surname = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.createdOn = createdAt;
    }

    public static UserResponse fromUser(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getLocation(), user.getCreatedAt());
    }
}
