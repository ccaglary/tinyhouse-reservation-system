package com.tinyhouse.repository;
import com.tinyhouse.entity.TinyHouse; import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.data.jpa.repository.Query; import org.springframework.data.repository.query.Param; import java.math.BigDecimal; import java.util.List; import java.util.Optional;
public interface TinyHouseRepository extends JpaRepository<TinyHouse, Long> {
    @Query("SELECT t FROM TinyHouse t LEFT JOIN FETCH t.images LEFT JOIN FETCH t.owner WHERE t.id = :id AND t.deleted = false")
    Optional<TinyHouse> findByIdAndDeletedFalse(@Param("id") Long id);

    @Query(value = "SELECT t FROM TinyHouse t LEFT JOIN FETCH t.images LEFT JOIN FETCH t.owner WHERE t.active = true AND t.deleted = false",
           countQuery = "SELECT count(t) FROM TinyHouse t WHERE t.active = true AND t.deleted = false")
    Page<TinyHouse> findByActiveTrueAndDeletedFalse(Pageable pageable);

    @Query(value = "SELECT t FROM TinyHouse t LEFT JOIN FETCH t.images LEFT JOIN FETCH t.owner WHERE t.owner.id = :ownerId AND t.deleted = false",
           countQuery = "SELECT count(t) FROM TinyHouse t WHERE t.owner.id = :ownerId AND t.deleted = false")
    Page<TinyHouse> findByOwnerIdAndDeletedFalse(@Param("ownerId") Long ownerId, Pageable pageable);

    long countByDeletedFalse();
    long countByActiveTrueAndDeletedFalse();

    @Query("SELECT DISTINCT t.city FROM TinyHouse t WHERE t.active = true AND t.deleted = false ORDER BY t.city")
    List<String> findDistinctCities();

    @Query(value = "SELECT t FROM TinyHouse t LEFT JOIN FETCH t.images LEFT JOIN FETCH t.owner WHERE t.active = true AND t.deleted = false AND (:city IS NULL OR t.city = :city) AND (:minPrice IS NULL OR t.nightlyPrice >= :minPrice) AND (:maxPrice IS NULL OR t.nightlyPrice <= :maxPrice) AND (:capacity IS NULL OR t.capacity >= :capacity) AND (:wifi IS NULL OR t.wifi = :wifi) AND (:parking IS NULL OR t.parking = :parking) AND (:petAllowed IS NULL OR t.petAllowed = :petAllowed)",
           countQuery = "SELECT count(t) FROM TinyHouse t WHERE t.active = true AND t.deleted = false AND (:city IS NULL OR t.city = :city) AND (:minPrice IS NULL OR t.nightlyPrice >= :minPrice) AND (:maxPrice IS NULL OR t.nightlyPrice <= :maxPrice) AND (:capacity IS NULL OR t.capacity >= :capacity) AND (:wifi IS NULL OR t.wifi = :wifi) AND (:parking IS NULL OR t.parking = :parking) AND (:petAllowed IS NULL OR t.petAllowed = :petAllowed)")
    Page<TinyHouse> search(@Param("city") String city, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, @Param("capacity") Integer capacity, @Param("wifi") Boolean wifi, @Param("parking") Boolean parking, @Param("petAllowed") Boolean petAllowed, Pageable pageable);

    @Query(value = "SELECT t FROM TinyHouse t LEFT JOIN FETCH t.images LEFT JOIN FETCH t.owner WHERE t.active = true AND t.deleted = false AND (LOWER(t.title) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(t.city) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%',:q,'%')))",
           countQuery = "SELECT count(t) FROM TinyHouse t WHERE t.active = true AND t.deleted = false AND (LOWER(t.title) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(t.city) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%',:q,'%')))")
    Page<TinyHouse> searchByKeyword(@Param("q") String keyword, Pageable pageable);

    @Query("SELECT t FROM TinyHouse t LEFT JOIN FETCH t.images LEFT JOIN FETCH t.owner WHERE t.active = true AND t.deleted = false ORDER BY t.averageRating DESC")
    List<TinyHouse> findTopRated(Pageable pageable);
}
