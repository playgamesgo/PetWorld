package me.playgamesgo.petworldbackend.services;

import me.playgamesgo.petworldbackend.models.User;
import me.playgamesgo.petworldbackend.payload.request.UpdateUserRequest;
import me.playgamesgo.petworldbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void updateUser(Long id, UpdateUserRequest userDetails) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return;
        if (userDetails.getFirstName() != null) user.setFirstName(userDetails.getFirstName());
        if (userDetails.getLastName() != null) user.setLastName(userDetails.getLastName());
        if (userDetails.getEmail() != null) user.setEmail(userDetails.getEmail());
        if (userDetails.getPhoneNumber() != null) user.setPhoneNumber(userDetails.getPhoneNumber());
        if (userDetails.getLocation() != null) user.setLocation(userDetails.getLocation());
        if (userDetails.getPassword() != null) user.setPassword(userDetails.getPassword());
        userRepository.save(user);
    }
}