package me.playgamesgo.petworldbackend.controllers;

import me.playgamesgo.petworldbackend.models.Ad;
import me.playgamesgo.petworldbackend.models.User;
import me.playgamesgo.petworldbackend.payload.request.CreateAdRequest;
import me.playgamesgo.petworldbackend.payload.request.UpdateAdRequest;
import me.playgamesgo.petworldbackend.payload.response.AdResponse;
import me.playgamesgo.petworldbackend.payload.response.MessageResponse;
import me.playgamesgo.petworldbackend.services.AdService;
import me.playgamesgo.petworldbackend.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ads")
public class AdController {
    private final AdService adService;
    private final UserService userService;

    public AdController(AdService adRepository, UserService userService) {
        this.adService = adRepository;
        this.userService = userService;
    }

    @GetMapping
    public List<AdResponse> getAllAds(@RequestParam(defaultValue = "0") int from,
                                      @RequestParam(defaultValue = "20") int limit) {
        Pageable pageable = PageRequest.of(from, limit);
        Page<Ad> ads = adService.findAll(pageable);
        return AdResponse.fromList(ads.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdResponse> getAdById(@PathVariable Long id) {
        Ad ad = adService.findById(id);

        if (ad == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(AdResponse.fromAd(ad));
    }

    @PostMapping
    public ResponseEntity<AdResponse> createAd(@RequestBody CreateAdRequest ad) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<User> user = userService.findByUsername(currentUsername);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(AdResponse.fromAd(adService.save(ad, user.get())));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateAd(@PathVariable Long id, @RequestBody UpdateAdRequest adDetails) {
        if (!isUserAdOwner(id)) {
            return ResponseEntity.status(403).build();
        }

        if (adService.updateAd(id, adDetails)) {
            return ResponseEntity.ok(new MessageResponse("Ad updated successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable Long id) {
        if (!isUserAdOwner(id)) {
            return ResponseEntity.status(403).build();
        }

        if (adService.existsById(id)) {
            adService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<MessageResponse> activateAd(@PathVariable Long id) {
        if (!isUserAdOwner(id)) {
            return ResponseEntity.status(403).build();
        }

        Ad ad = adService.findById(id);
        if (ad != null) {
            ad.setActive(true);
            adService.save(ad);
            return ResponseEntity.ok(new MessageResponse("Ad activated successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<MessageResponse> deactivateAd(@PathVariable Long id) {
        if (!isUserAdOwner(id)) {
            return ResponseEntity.status(403).build();
        }

        Ad ad = adService.findById(id);
        if (ad != null) {
            ad.setActive(false);
            adService.save(ad);
            return ResponseEntity.ok(new MessageResponse("Ad deactivated successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public List<AdResponse> searchAds(@RequestParam(required = false) String type,
                                      @RequestParam(required = false) Integer age,
                                      @RequestParam(required = false) String gender,
                                      @RequestParam(required = false) String breed,
                                      @RequestParam(required = false) String size,
                                      @RequestParam(required = false) String location,
                                      @RequestParam(required = false) Boolean active,
                                      @RequestParam(defaultValue = "0") int from,
                                      @RequestParam(defaultValue = "20") int limit,
                                      @RequestParam(required = false) String sortBy) {
        Sort sort = Sort.by(Sort.Direction.DESC, "relevance");
        if (sortBy != null) {
            sort = switch (sortBy) {
                case "date" -> Sort.by(Sort.Direction.DESC, "createdAt");
                case "distance" -> Sort.by(Sort.Direction.ASC, "location");
                case "price" -> Sort.by(Sort.Direction.ASC, "price");
                default -> sort;
            };
        }
        Pageable pageable = PageRequest.of(from, limit, sort);
        Page<Ad> ads = adService.findAdsByFilters(type, age, gender, breed, size, location, active, pageable);
        return AdResponse.fromList(ads.getContent());
    }

    private boolean isUserAdOwner(Long adId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<User> user = userService.findByUsername(currentUsername);

        return user.filter(value -> value == adService.findById(adId).getUser()).isPresent();
    }
}