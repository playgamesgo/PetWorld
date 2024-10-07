package me.playgamesgo.petworldbackend.services;

import me.playgamesgo.petworldbackend.models.Ad;
import me.playgamesgo.petworldbackend.models.PetProperty;
import me.playgamesgo.petworldbackend.models.Photo;
import me.playgamesgo.petworldbackend.models.User;
import me.playgamesgo.petworldbackend.payload.request.AddAdRequest;
import me.playgamesgo.petworldbackend.payload.request.UpdateAdRequest;
import me.playgamesgo.petworldbackend.repository.AdRepository;
import me.playgamesgo.petworldbackend.repository.PetPropertyRepository;
import me.playgamesgo.petworldbackend.repository.PhotoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing ads.
 */
@Service
public class AdService {
    private final AdRepository adRepository;
    private final PhotoRepository photoRepository;
    private final PetPropertyRepository petPropertyRepository;

    public AdService(AdRepository adRepository, PhotoRepository photoRepository, PetPropertyRepository petPropertyRepository) {
        this.adRepository = adRepository;
        this.photoRepository = photoRepository;
        this.petPropertyRepository = petPropertyRepository;
    }

    /**
     * Finds an Ad by its ID.
     *
     * @param id the ID of the Ad to find.
     * @return an Optional containing the found Ad, or empty if not found.
     */
    public Optional<Ad> findById(Long id) {
        return adRepository.findById(id);
    }

    /**
     * Creates a new Ad.
     *
     * @param addAdRequest the request payload containing Ad details.
     * @param user the user creating the Ad.
     * @return the created Ad.
     */
    public Ad createAd(AddAdRequest addAdRequest, User user) {
        // Create the Ad.
        Ad ad = new Ad();
        ad.setIsActive(addAdRequest.getIsActive());
        ad.setPetOrigin(addAdRequest.getPetOrigin());
        ad.setTitle(addAdRequest.getTitle());
        ad.setPrice(addAdRequest.getPrice());
        ad.setSummary(addAdRequest.getSummary());
        ad.setLocation(addAdRequest.getLocation());
        ad.setAge(addAdRequest.getAge());
        ad.setAgeUnits(addAdRequest.getAgeUnits());
        ad.setAppUser(user);

        ad = adRepository.save(ad);

        // Create the Photos and Properties.
        Ad finalAd = ad;
        List<Photo> photos = addAdRequest.getPhotos().stream()
                .map(photoRequest -> {
                    Photo photo = new Photo();
                    photo.setUrl(photoRequest.getUrl());
                    photo.setAd(finalAd);
                    return photo;
                }).collect(Collectors.toList());
        photoRepository.saveAll(photos);

        List<PetProperty> properties = addAdRequest.getProperties().stream()
                .map(propertyRequest -> {
                    PetProperty property = new PetProperty();
                    property.setCustomValue(propertyRequest.getCustomValue());
                    property.setPropertyDefinition(propertyRequest.getPropertyDefinition());
                    property.setPredefinedValue(propertyRequest.getPredefinedValue());
                    property.setAd(finalAd);
                    return property;
                }).collect(Collectors.toList());
        petPropertyRepository.saveAll(properties);

        ad.setPhotos(photos);
        ad.setProperties(properties);

        return adRepository.save(ad);
    }

    /**
     * Updates an existing Ad.
     *
     * @param id the ID of the Ad to update.
     * @param updateAdRequest the request payload containing updated Ad details.
     * @return true if the Ad was updated, false otherwise.
     */
    public boolean updateAd(Long id, UpdateAdRequest updateAdRequest) {
        // Find the Ad.
        Optional<Ad> optionalAd = adRepository.findById(id);

        // If the Ad does not exist, return false.
        if (optionalAd.isEmpty()) return false;

        // Update the Ad.
        Ad ad = optionalAd.get();
        if (updateAdRequest.getIsActive() != null) ad.setIsActive(updateAdRequest.getIsActive());
        if (updateAdRequest.getPetOrigin() != null) ad.setPetOrigin(updateAdRequest.getPetOrigin());
        if (updateAdRequest.getTitle() != null) ad.setTitle(updateAdRequest.getTitle());
        if (updateAdRequest.getPrice() != null) ad.setPrice(updateAdRequest.getPrice());
        if (updateAdRequest.getSummary() != null) ad.setSummary(updateAdRequest.getSummary());
        if (updateAdRequest.getLocation() != null) ad.setLocation(updateAdRequest.getLocation());
        if (updateAdRequest.getAge() != null) ad.setAge(updateAdRequest.getAge());
        if (updateAdRequest.getAgeUnits() != null) ad.setAgeUnits(updateAdRequest.getAgeUnits());
        ad.setLastModifiedOn(new Date());

        ad = adRepository.save(ad);

        // Update the Photos and Properties.
        Ad finalAd = ad;
        if (updateAdRequest.getPhotos() != null) {
            photoRepository.deleteAllByAdId(ad.getId());
            List<Photo> photos = updateAdRequest.getPhotos().stream()
                    .map(photoRequest -> {
                        Photo photo = new Photo();
                        photo.setUrl(photoRequest.getUrl());
                        photo.setAd(finalAd);
                        return photo;
                    }).collect(Collectors.toList());
            photoRepository.saveAll(photos);
            ad.setPhotos(photos);
        }

        if (updateAdRequest.getProperties() != null) {
            petPropertyRepository.deleteAllByAdId(ad.getId());
            List<PetProperty> properties = updateAdRequest.getProperties().stream()
                    .map(propertyRequest -> {
                        PetProperty property = new PetProperty();
                        property.setCustomValue(propertyRequest.getCustomValue());
                        property.setPropertyDefinition(propertyRequest.getPropertyDefinition());
                        property.setPredefinedValue(propertyRequest.getPredefinedValue());
                        property.setAd(finalAd);
                        return property;
                    }).collect(Collectors.toList());
            petPropertyRepository.saveAll(properties);
            ad.setProperties(properties);
        }

        adRepository.save(ad);

        return true;
    }

    /**
     * Checks if an Ad exists by its ID.
     *
     * @param id the ID of the Ad to check.
     * @return true if the Ad exists, false otherwise.
     */
    public boolean existsById(Long id) {
        return adRepository.existsById(id);
    }

    /**
     * Deletes an Ad by its ID.
     *
     * @param id the ID of the Ad to delete.
     */
    @Transactional
    public void deleteById(Long id) {
        petPropertyRepository.deleteAllByAdId(id);
        photoRepository.deleteAllByAdId(id);
        adRepository.deleteById(id);
    }

    /**
     * Finds all Ads with pagination and filtering.
     *
     * @param spec the specification for filtering Ads.
     * @param pageable the pagination information.
     * @return a page of Ads.
     */
    public Page<Ad> findAll(Specification<Ad> spec, Pageable pageable) {
        return adRepository.findAll(spec, pageable);
    }

    /**
     * Finds all Ads.
     *
     * @return a list of all Ads.
     */
    public List<Ad> findAll() {
        return adRepository.findAll();
    }
}