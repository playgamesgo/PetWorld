package me.playgamesgo.petworldbackend.controllers;

import jakarta.validation.Valid;
import me.playgamesgo.petworldbackend.models.Role;
import me.playgamesgo.petworldbackend.models.Roles;
import me.playgamesgo.petworldbackend.models.User;
import me.playgamesgo.petworldbackend.payload.request.ForgotPasswordRequest;
import me.playgamesgo.petworldbackend.payload.request.LoginRequest;
import me.playgamesgo.petworldbackend.payload.request.ResetPasswordRequest;
import me.playgamesgo.petworldbackend.payload.request.SignupRequest;
import me.playgamesgo.petworldbackend.payload.response.JwtResponse;
import me.playgamesgo.petworldbackend.payload.response.MessageResponse;
import me.playgamesgo.petworldbackend.payload.response.UserResponse;
import me.playgamesgo.petworldbackend.repository.RoleRepository;
import me.playgamesgo.petworldbackend.repository.UserRepository;
import me.playgamesgo.petworldbackend.security.jwt.JwtUtils;
import me.playgamesgo.petworldbackend.services.EmailService;
import me.playgamesgo.petworldbackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Controller for handling authentication-related requests.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, String> redisTemplate;
    private final EmailService emailService;
    private final UserService userService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils, @Qualifier("redisTemplate") RedisTemplate<String, String> redisTemplate, EmailService emailService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.redisTemplate = redisTemplate;
        this.emailService = emailService;
        this.userService = userService;
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param loginRequest the login request payload
     * @return a response containing the JWT token
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // Set the authentication object in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    /**
     * Registers a new user.
     *
     * @param signUpRequest the signup request payload
     * @return a response containing the registered user details
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // Check if the username is already taken
        if (userRepository.existsByUsername(signUpRequest.getLogin())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        // Check if the email is already in use
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create a new user
        User user = new User(signUpRequest.getLogin(),
                signUpRequest.getName(),
                signUpRequest.getSurname(),
                signUpRequest.getEmail(),
                signUpRequest.getPhoneNumber(),
                signUpRequest.getLocation(),
                encoder.encode(signUpRequest.getPassword()));

        // Set the user role
        Role userRole = roleRepository.findByName(Roles.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        user.setRole(userRole);
        userRepository.save(user);

        return ResponseEntity.ok(UserResponse.fromUser(user));
    }

    /**
     * Handles forgot password requests.
     *
     * @param forgotPasswordRequest the forgot password request payload
     * @return a response indicating the result of the operation
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        // Check if the user exists
        String email = forgotPasswordRequest.getEmail();
        User user = userRepository.findByEmail(email)
                .orElse(null);

        // Send the password reset link to the user's email
        if (user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found."));
        }

        // Generate a token and store it in Redis
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(token, user.getUsername(), 15, TimeUnit.MINUTES);

        // Send the password reset link to the user's email
        String resetLink = frontendUrl + "/#/auth/create-new-password?token=" + token;
        emailService.sendEmail(email, "Password Reset Request", "To reset your password, click the link below:\n" + resetLink);

        return ResponseEntity.ok(new MessageResponse("Password reset link sent to your email."));
    }

    /**
     * Resets the user's password.
     *
     * @param resetPasswordRequest the reset password request payload
     * @return a response indicating the result of the operation
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        // Check if the token is valid
        String token = resetPasswordRequest.getToken();
        String newPassword = resetPasswordRequest.getNewPassword();

        // Check if the token is valid
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Token is required."));
        }

        // Check if the token is expired
        String username = redisTemplate.opsForValue().get(token);
        if (username == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid or expired token."));
        }

        // Update the user's password
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);

        // Delete the token from Redis
        redisTemplate.delete(token);

        return ResponseEntity.ok(new MessageResponse("Password reset successfully."));
    }

    /**
     * Retrieves the current authenticated user's details.
     *
     * @return a response containing the user details
     */
    @GetMapping("/me")
    public ResponseEntity<?> me() {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Optional<User> user = userService.findByUsername(currentUsername);

        // Return the user details
        if (user.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(UserResponse.fromUser(user.get()));
    }
}