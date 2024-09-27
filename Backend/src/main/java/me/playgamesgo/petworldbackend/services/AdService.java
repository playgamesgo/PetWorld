package me.playgamesgo.petworldbackend.services;

import me.playgamesgo.petworldbackend.models.Ad;
import me.playgamesgo.petworldbackend.models.User;
import me.playgamesgo.petworldbackend.payload.request.CreateAdRequest;
import me.playgamesgo.petworldbackend.payload.request.UpdateAdRequest;
import me.playgamesgo.petworldbackend.repository.AdRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AdService {
    private final AdRepository adRepository;

    public AdService(AdRepository adRepository) {
        this.adRepository = adRepository;
    }

    public Page<Ad> findAll(Pageable pageable) {
        return adRepository.findAll(pageable);
    }

    public void save(CreateAdRequest ad, User user) {
        Ad newAd = new Ad(user, ad.getTitle(), ad.getDescription(), ad.getType(), ad.getPrice(), ad.getLocation(),
                ad.getAge(), ad.getBreed(), ad.getGender(), ad.getVaccinated(), ad.getHealth(),
                ad.getColor(), ad.getSize(), ad.getFurLength(), ad.getImages());
        adRepository.save(newAd);
    }

    public void save(Ad ad) {
        adRepository.save(ad);
    }

    public Ad findById(Long id) {
        return adRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        adRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return adRepository.existsById(id);
    }

    public boolean updateAd(long adId, UpdateAdRequest adDetails) {
        Ad ad = adRepository.findById(adId).orElse(null);
        if (ad == null) return false;
        if (adDetails.getTitle() != null) ad.setTitle(adDetails.getTitle());
        if (adDetails.getDescription() != null) ad.setDescription(adDetails.getDescription());
        if (adDetails.getPrice() != null) ad.setPrice(adDetails.getPrice());
        if (adDetails.getLocation() != null) ad.setLocation(adDetails.getLocation());
        if (adDetails.getAge() != null) ad.setAge(adDetails.getAge());
        if (adDetails.getBreed() != null) ad.setBreed(adDetails.getBreed());
        if (adDetails.getGender() != null) ad.setGender(adDetails.getGender());
        if (adDetails.getVaccinated() != null) ad.setVaccinated(adDetails.getVaccinated());
        if (adDetails.getHealth() != null) ad.setHealth(adDetails.getHealth());
        if (adDetails.getColor() != null) ad.setColor(adDetails.getColor());
        if (adDetails.getSize() != null) ad.setSize(adDetails.getSize());
        if (adDetails.getFurLength() != null) ad.setFurLength(adDetails.getFurLength());
        if (adDetails.getImages() != null) ad.setImages(adDetails.getImages());
        adRepository.save(ad);
        return true;
    }

    public Page<Ad> findAdsByFilters(String type, Integer age, String gender, String breed, String size, String location, Boolean active, Pageable pageable) {
        return adRepository.findAdsByFilters(type, age, gender, breed, size, location, active, pageable);
    }
}