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
    public ListAdResponse getAllAds(@RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = Integer.MAX_VALUE + "") int limit,
                                    @RequestParam(required = false) String $filter,
                                    @RequestParam(required = false) String $orderby) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdOn");
        if ($orderby != null) {
            List<Sort.Order> orders = new ArrayList<>();
            for (String order : $orderby.split(",")) {
                String[] parts = order.split(" ");
                if (parts.length == 2) {
                    Sort.Direction direction = parts[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
                    orders.add(new Sort.Order(direction, parts[0]));
                } else {
                    orders.add(new Sort.Order(Sort.Direction.ASC, parts[0]));
                }
            }
            sort = Sort.by(orders);
        }
        Pageable pageable = PageRequest.of(from, limit, sort);
        Specification<Ad> spec = FilterParser.parseFilter($filter);
        Page<Ad> ads = adService.findAll(spec, pageable);
        if (ads == null || ads.isEmpty()) {
            return new ListAdResponse(new ArrayList<>());
        }
        return AdResponse.fromList(ads.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdResponse> getAdById(@PathVariable Long id) {
        Optional<Ad> ad = adService.findById(id);

        return ad.map(value -> ResponseEntity.ok(AdResponse.fromAd(value))).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @PostMapping
    public ResponseEntity<AdResponse> createAd(@RequestBody AddAdRequest ad) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<User> user = userService.findByUsername(currentUsername);
        return user.map(value -> ResponseEntity.ok(AdResponse.fromAd(adService.createAd(ad, value)))).orElseGet(() -> ResponseEntity.status(403).build());

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
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private boolean isUserAdOwner(Long adId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<User> user = userService.findByUsername(currentUsername);
        if (user.isEmpty()) {
            return false;
        }

        Optional<Ad> ad = adService.findById(adId);
        return ad.filter(value -> user.get().equals(value.getAppUser())).isPresent();

    }
}