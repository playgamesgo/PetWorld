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

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(new UserResponse(user.get().getId(), user.get().getUsername(), user.get().getFirstName(), user.get().getLastName(),
                    user.get().getEmail(), user.get().getPhoneNumber(), user.get().getLocation(), user.get().getCreatedAt()));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<User> user = userService.getUserById(id);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }

        if (!user.get().getUsername().equals(currentUsername)) {
            return ResponseEntity.status(403).body(new MessageResponse("You are not authorized to delete this user"));
        }

        userService.deleteUserById(id);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<User> user = userService.getUserById(id);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
        }

        if (!user.get().getUsername().equals(currentUsername)) {
            return ResponseEntity.status(403).body(new MessageResponse("You are not authorized to edit this user"));
        }

        userService.updateUser(id, userDetails);
        return ResponseEntity.ok(new MessageResponse("User updated successfully"));
    }
}