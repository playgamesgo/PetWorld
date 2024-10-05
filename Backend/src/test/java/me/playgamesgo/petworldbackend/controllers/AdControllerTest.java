package me.playgamesgo.petworldbackend.controllers;

import me.playgamesgo.petworldbackend.models.Ad;
import me.playgamesgo.petworldbackend.models.User;
import me.playgamesgo.petworldbackend.payload.request.AddAdRequest;
import me.playgamesgo.petworldbackend.payload.request.UpdateAdRequest;
import me.playgamesgo.petworldbackend.payload.response.AdResponse;
import me.playgamesgo.petworldbackend.payload.response.ListAdResponse;
import me.playgamesgo.petworldbackend.payload.response.MessageResponse;
import me.playgamesgo.petworldbackend.services.AdService;
import me.playgamesgo.petworldbackend.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdControllerTest {
    private AutoCloseable closeable;

    @Mock
    private AdService adService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AdController adController;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @AfterEach
    void close() throws Exception {
        closeable.close();
    }

    @Test
    void getAllAds_returnsAds() {
        Page<Ad> ads = Page.empty();
        when(adService.findAll(any(Specification.class), any(Pageable.class))).thenReturn(ads);

        ListAdResponse response = adController.getAllAds(0, 10, null, null);

        assertNotNull(response);
    }

    @Test
    void getAdById_returnsAd() {
        Ad ad = new Ad();
        ad.setId(1L);
        when(adService.findById(1L)).thenReturn(Optional.of(ad));

        ResponseEntity<AdResponse> response = adController.getAdById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void getAdById_returnsNotFound() {
        when(adService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<AdResponse> response = adController.getAdById(1L);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void createAd_createsAd() {
        AddAdRequest addAdRequest = new AddAdRequest();
        User user = new User();
        user.setUsername("testUser");
        when(authentication.getName()).thenReturn("testUser");
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(adService.createAd(any(AddAdRequest.class), any(User.class))).thenReturn(new Ad());

        ResponseEntity<AdResponse> response = adController.createAd(addAdRequest);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void createAd_returnsForbidden() {
        AddAdRequest addAdRequest = new AddAdRequest();
        when(authentication.getName()).thenReturn("testUser");
        when(userService.findByUsername("testUser")).thenReturn(Optional.empty());

        ResponseEntity<AdResponse> response = adController.createAd(addAdRequest);

        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void updateAd_updatesAd() {
        UpdateAdRequest updateAdRequest = new UpdateAdRequest();
        Ad ad = new Ad();
        User user = new User();
        user.setUsername("testUser");
        ad.setAppUser(user);
        when(authentication.getName()).thenReturn("testUser");
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(adService.findById(1L)).thenReturn(Optional.of(ad));
        when(adService.updateAd(1L, updateAdRequest)).thenReturn(true);

        ResponseEntity<MessageResponse> response = adController.updateAd(1L, updateAdRequest);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Ad updated successfully", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void updateAd_returnsForbidden() {
        UpdateAdRequest updateAdRequest = new UpdateAdRequest();
        when(authentication.getName()).thenReturn("testUser");
        when(userService.findByUsername("testUser")).thenReturn(Optional.empty());

        ResponseEntity<MessageResponse> response = adController.updateAd(1L, updateAdRequest);

        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void deleteAd_deletesAd() {
        Ad ad = new Ad();
        User user = new User();
        user.setUsername("testUser");
        ad.setAppUser(user);
        when(authentication.getName()).thenReturn("testUser");
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(adService.findById(1L)).thenReturn(Optional.of(ad));
        when(adService.existsById(1L)).thenReturn(true);

        ResponseEntity<Void> response = adController.deleteAd(1L);

        assertEquals(200, response.getStatusCode().value());
        verify(adService, times(1)).deleteById(1L);
    }

    @Test
    void deleteAd_returnsForbidden() {
        when(authentication.getName()).thenReturn("testUser");
        when(userService.findByUsername("testUser")).thenReturn(Optional.empty());

        ResponseEntity<Void> response = adController.deleteAd(1L);

        assertEquals(403, response.getStatusCode().value());
    }
}