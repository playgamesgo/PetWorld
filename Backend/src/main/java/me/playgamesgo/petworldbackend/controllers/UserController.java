package me.playgamesgo.petworldbackend.controllers;

import me.playgamesgo.petworldbackend.models.User;
import me.playgamesgo.petworldbackend.payload.request.UpdateUserRequest;
import me.playgamesgo.petworldbackend.payload.response.MessageResponse;
import me.playgamesgo.petworldbackend.payload.response.UserResponse;
import me.playgamesgo.petworldbackend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller for handling user-related requests.
 */
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user
     * @return a response containing the user details or an error message
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        // Get the user details
        Optional<User> user = userService.getUserById(id);
        // If the user exists, return the user details
        if (user.isPresent()) {
            return ResponseEntity.ok(new UserResponse(user.get().getId(), user.get().getUsername(), user.get().getFirstName(), user.get().getLastName(),
                    user.get().getEmail(), user.get().getPhoneNumber(), user.get().getLocation(), user.get().getCreatedAt()));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user
     * @return a response indicating the result of the delete operation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        // Get the current user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Get the user details
        Optional<User> user = userService.getUserById(id);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }

        // Check if the current user is authorized to delete the user
        if (!user.get().getUsername().equals(currentUsername)) {
            return ResponseEntity.status(403).body(new MessageResponse("You are not authorized to delete this user"));
        }

        // Delete the user
        userService.deleteUserById(id);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
    }

    /**
     * Updates a user's details.
     *
     * @param id the ID of the user to update
     * @param userDetails the user update request payload
     * @return a response indicating the result of the update operation
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest userDetails) {
        // Get the current user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Get the user details
        Optional<User> user = userService.getUserById(id);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }

        // Check if the current user is authorized to edit the user
        if (!user.get().getUsername().equals(currentUsername)) {
            return ResponseEntity.status(403).body(new MessageResponse("You are not authorized to edit this user"));
        }

        // Update the user
        userService.updateUser(id, userDetails);
        return ResponseEntity.ok(new MessageResponse("User updated successfully"));
    }
}