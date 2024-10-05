package me.playgamesgo.petworldbackend.controllers;

import me.playgamesgo.petworldbackend.models.User;
import me.playgamesgo.petworldbackend.payload.request.UpdateUserRequest;
import me.playgamesgo.petworldbackend.payload.response.MessageResponse;
import me.playgamesgo.petworldbackend.payload.response.UserResponse;
import me.playgamesgo.petworldbackend.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {
    private AutoCloseable closeable;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void getUserById_returnsUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userController.getUserById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertInstanceOf(UserResponse.class, response.getBody());
        assertEquals("testUser", ((UserResponse) response.getBody()).getLogin());
    }

    @Test
    void getUserById_returnsNotFound() {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = userController.getUserById(1L);

        assertEquals(400, response.getStatusCode().value());
        assertInstanceOf(MessageResponse.class, response.getBody());
        assertEquals("User not found", ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    void deleteUserById_deletesUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        when(authentication.getName()).thenReturn("testUser");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userController.deleteUserById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertInstanceOf(MessageResponse.class, response.getBody());
        assertEquals("User deleted successfully", ((MessageResponse) response.getBody()).getMessage());
        verify(userService, times(1)).deleteUserById(1L);
    }

    @Test
    void deleteUserById_returnsForbidden() {
        User user = new User();
        user.setId(1L);
        user.setUsername("anotherUser");
        when(authentication.getName()).thenReturn("testUser");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userController.deleteUserById(1L);

        assertEquals(403, response.getStatusCode().value());
        assertInstanceOf(MessageResponse.class, response.getBody());
        assertEquals("You are not authorized to delete this user", ((MessageResponse) response.getBody()).getMessage());
    }

    @Test
    void updateUser_updatesUser() {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setFirstName("UpdatedFirstName");
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        when(authentication.getName()).thenReturn("testUser");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userController.updateUser(1L, updateUserRequest);

        assertEquals(200, response.getStatusCode().value());
        assertInstanceOf(MessageResponse.class, response.getBody());
        assertEquals("User updated successfully", ((MessageResponse) response.getBody()).getMessage());
        verify(userService, times(1)).updateUser(1L, updateUserRequest);
    }

    @Test
    void updateUser_returnsForbidden() {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        User user = new User();
        user.setId(1L);
        user.setUsername("anotherUser");
        when(authentication.getName()).thenReturn("testUser");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userController.updateUser(1L, updateUserRequest);

        assertEquals(403, response.getStatusCode().value());
        assertInstanceOf(MessageResponse.class, response.getBody());
        assertEquals("You are not authorized to edit this user", ((MessageResponse) response.getBody()).getMessage());
    }
}