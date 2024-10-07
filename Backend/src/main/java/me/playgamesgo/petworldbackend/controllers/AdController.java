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
import me.playgamesgo.petworldbackend.specifications.FilterParser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for handling ad-related requests.
 */
@RestController
@RequestMapping("/ads")
public class AdController {
    private final AdService adService;
    private final UserService userService;

    public AdController(AdService adRepository, UserService userService) {
        this.adService = adRepository;
        this.userService = userService;
    }

    /**
     * Retrieves all ads with optional filtering and sorting.
     *
     * @param from     the starting index for pagination
     * @param limit    the maximum number of ads to return
     * @param $filter  the filter criteria
     * @param $orderby the sorting criteria
     * @return a response containing the list of ads
     */
    @GetMapping
    public ListAdResponse getAllAds(@RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = Integer.MAX_VALUE + "") int limit,
                                    @RequestParam(required = false) String $filter,
                                    @RequestParam(required = false) String $orderby) {
        // Default sort by createdOn descending
        Sort sort = Sort.by(Sort.Direction.DESC, "createdOn");
        if ($orderby != null) {
            // Parse the sorting criteria
            List<Sort.Order> orders = new ArrayList<>();
            for (String order : $orderby.split(",")) {
                String[] parts = order.split(" ");
                if (parts.length == 2) {
                    // If a direction is specified use it, otherwise default to ascending
                    Sort.Direction direction = parts[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
                    orders.add(new Sort.Order(direction, parts[0]));
                } else {
                    orders.add(new Sort.Order(Sort.Direction.ASC, parts[0]));
                }
            }
            sort = Sort.by(orders);
        }
        // Create a pageable object for pagination
        Pageable pageable = PageRequest.of(from, limit, sort);
        Specification<Ad> spec = FilterParser.parseFilter($filter);
        Page<Ad> ads = adService.findAll(spec, pageable);

        if (ads == null || ads.isEmpty()) {
            return new ListAdResponse(new ArrayList<>());
        }
        return AdResponse.fromList(ads.getContent());
    }

    /**
     * Retrieves an ad by its ID.
     *
     * @param id the ID of the ad
     * @return a response containing the ad details
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdResponse> getAdById(@PathVariable Long id) {
        // Find the ad by its ID
        Optional<Ad> ad = adService.findById(id);
        return ad.map(value -> ResponseEntity.ok(AdResponse.fromAd(value))).orElseGet(() -> ResponseEntity.notFound().build());

    }

    /**
     * Creates a new ad.
     *
     * @param ad the ad request payload
     * @return a response containing the created ad details
     */
    @PostMapping
    public ResponseEntity<AdResponse> createAd(@RequestBody AddAdRequest ad) {
        // Get the current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<User> user = userService.findByUsername(currentUsername);
        return user.map(value -> ResponseEntity.ok(AdResponse.fromAd(adService.createAd(ad, value)))).orElseGet(() -> ResponseEntity.status(403).build());

    }

    /**
     * Updates an existing ad.
     *
     * @param id the ID of the ad to update
     * @param adDetails the ad update request payload
     * @return a response indicating the result of the update operation
     */
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateAd(@PathVariable Long id, @RequestBody UpdateAdRequest adDetails) {
        // Check if the current user is the owner of the ad
        if (!isUserAdOwner(id)) {
            return ResponseEntity.status(403).build();
        }

        // Update the ad
        if (adService.updateAd(id, adDetails)) {
            return ResponseEntity.ok(new MessageResponse("Ad updated successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes an ad by its ID.
     *
     * @param id the ID of the ad to delete
     * @return a response indicating the result of the delete operation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable Long id) {
        // Check if the current user is the owner of the ad
        if (!isUserAdOwner(id)) {
            return ResponseEntity.status(403).build();
        }

        // Delete the ad
        if (adService.existsById(id)) {
            adService.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Checks if the current user is the owner of the specified ad.
     *
     * @param adId the ID of the ad
     * @return true if the current user is the owner, false otherwise
     */
    private boolean isUserAdOwner(Long adId) {
        // Get the current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Find the user by username
        Optional<User> user = userService.findByUsername(currentUsername);
        if (user.isEmpty()) {
            return false;
        }

        // Find the ad by ID
        Optional<Ad> ad = adService.findById(adId);
        return ad.filter(value -> user.get().equals(value.getAppUser())).isPresent();

    }
}