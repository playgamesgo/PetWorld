package me.playgamesgo.petworldbackend.repository;

import me.playgamesgo.petworldbackend.models.Ad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdRepository extends JpaRepository<Ad, Long> {

    @Query("SELECT a FROM Ad a WHERE " +
            "(:type IS NULL OR a.type = :type) AND " +
            "(:age IS NULL OR a.age = :age) AND " +
            "(:gender IS NULL OR a.gender = :gender) AND " +
            "(:breed IS NULL OR a.breed = :breed) AND " +
            "(:size IS NULL OR a.size = :size) AND " +
            "(:location IS NULL OR a.location = :location) AND " +
            "(:active IS NULL OR a.active = :active)")
    Page<Ad> findAdsByFilters(@Param("type") String type,
                              @Param("age") Integer age,
                              @Param("gender") String gender,
                              @Param("breed") String breed,
                              @Param("size") String size,
                              @Param("location") String location,
                              @Param("active") Boolean active,
                              Pageable pageable);
}