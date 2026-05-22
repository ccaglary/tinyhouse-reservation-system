package com.tinyhouse.repository;
import com.tinyhouse.entity.Review; import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.data.jpa.repository.Query; import org.springframework.data.repository.query.Param; import java.math.BigDecimal;
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByTinyHouseIdAndDeletedFalse(Long tinyHouseId, Pageable pageable);
    Page<Review> findByTenantIdAndDeletedFalse(Long tenantId, Pageable pageable);
    boolean existsByTenantIdAndTinyHouseIdAndDeletedFalse(Long tenantId, Long tinyHouseId);
    long countByDeletedFalse();
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.tinyHouse.id = :houseId AND r.deleted = false")
    BigDecimal calculateAverageRating(@Param("houseId") Long houseId);
}
