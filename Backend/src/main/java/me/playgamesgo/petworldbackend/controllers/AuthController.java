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

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getLogin())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User(signUpRequest.getLogin(),
                signUpRequest.getName(),
                signUpRequest.getSurname(),
                signUpRequest.getEmail(),
                signUpRequest.getPhoneNumber(),
                signUpRequest.getLocation(),
                encoder.encode(signUpRequest.getPassword()));

        Role userRole = roleRepository.findByName(Roles.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        user.setRole(userRole);
        userRepository.save(user);

        return ResponseEntity.ok(UserResponse.fromUser(user));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        String email = forgotPasswordRequest.getEmail();
        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found."));
        }

        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(token, user.getUsername(), 15, TimeUnit.MINUTES);

        String resetLink = frontendUrl + "/#/auth/create-new-password?token=" + token;
        emailService.sendEmail(email, "Password Reset Request", "To reset your password, click the link below:\n" + resetLink);

        return ResponseEntity.ok(new MessageResponse("Password reset link sent to your email."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        String token = resetPasswordRequest.getToken();
        String newPassword = resetPasswordRequest.getNewPassword();

        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Token is required."));
        }

        String username = redisTemplate.opsForValue().get(token);
        if (username == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid or expired token."));
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);

        redisTemplate.delete(token);

        return ResponseEntity.ok(new MessageResponse("Password reset successfully."));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Optional<User> user = userService.findByUsername(currentUsername);

        if (user.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(UserResponse.fromUser(user.get()));
    }
}