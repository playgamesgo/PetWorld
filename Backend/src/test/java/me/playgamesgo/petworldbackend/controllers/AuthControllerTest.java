package me.playgamesgo.petworldbackend.controllers;

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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {
    private AutoCloseable closeable;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.findByUsername(anyString())).thenReturn(Optional.empty());
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @AfterEach
    void close() throws Exception {
        closeable.close();
    }

    @Test
    void authenticateUser_returnsJwtResponse() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("testPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("testToken");

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("testToken", ((JwtResponse) response.getBody()).getAccessToken());
    }

    @Test
    void registerUser_returnsUserResponse() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setLogin("testUser");
        signupRequest.setName("Test");
        signupRequest.setSurname("User");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPhoneNumber("1234567890");
        signupRequest.setLocation("Test Location");
        signupRequest.setPassword("testPassword");

        when(userRepository.existsByUsername("testUser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(roleRepository.findByName(Roles.USER)).thenReturn(Optional.of(new Role()));
        when(encoder.encode("testPassword")).thenReturn("encodedPassword");

        User user = new User();
        user.setUsername("testUser");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");
        user.setLocation("Test Location");
        user.setPassword("encodedPassword");

        // Mock the save method to set the id
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        ResponseEntity<?> response = authController.registerUser(signupRequest);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("testUser", ((UserResponse) response.getBody()).getLogin());
    }

    @Test
    void registerUser_returnsUsernameTakenError() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setLogin("testUser");
        signupRequest.setEmail("test@example.com");

        when(userRepository.existsByUsername("testUser")).thenReturn(true);

        ResponseEntity<?> response = authController.registerUser(signupRequest);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Error: Username is already taken!", ((MessageResponse) Objects.requireNonNull(response.getBody())).getMessage());
    }

    @Test
    void forgotPassword_sendsResetLink() {
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
        forgotPasswordRequest.setEmail("test@example.com");

        User user = new User();
        user.setUsername("testUser");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

        ResponseEntity<?> response = authController.forgotPassword(forgotPasswordRequest);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Password reset link sent to your email.", ((MessageResponse) Objects.requireNonNull(response.getBody())).getMessage());
    }

    @Test
    void forgotPassword_returnsUserNotFoundError() {
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
        forgotPasswordRequest.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.forgotPassword(forgotPasswordRequest);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Error: User not found.", ((MessageResponse) Objects.requireNonNull(response.getBody())).getMessage());
    }

    @Test
    void resetPassword_resetsPasswordSuccessfully() {
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest();
        resetPasswordRequest.setToken("testToken");
        resetPasswordRequest.setNewPassword("newPassword");

        when(redisTemplate.opsForValue().get("testToken")).thenReturn("testUser");
        User user = new User();
        user.setUsername("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(encoder.encode("newPassword")).thenReturn("encodedNewPassword");

        ResponseEntity<?> response = authController.resetPassword(resetPasswordRequest);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Password reset successfully.", ((MessageResponse) Objects.requireNonNull(response.getBody())).getMessage());
    }

    @Test
    void resetPassword_returnsInvalidTokenError() {
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest();
        resetPasswordRequest.setToken("invalidToken");
        resetPasswordRequest.setNewPassword("newPassword");

        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("invalidToken")).thenReturn(null);

        ResponseEntity<?> response = authController.resetPassword(resetPasswordRequest);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Error: Invalid or expired token.", ((MessageResponse) Objects.requireNonNull(response.getBody())).getMessage());
    }

    @Test
    void me_returnsUserResponse() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        when(authentication.getName()).thenReturn("testUser");
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = authController.me();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("testUser", ((UserResponse) response.getBody()).getLogin());
    }
}