package com.tinyhouse.repository;
import com.tinyhouse.entity.Favorite; import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserIdAndTinyHouseId(Long userId, Long tinyHouseId);
    Page<Favorite> findByUserIdAndDeletedFalse(Long userId, Pageable pageable);
    boolean existsByUserIdAndTinyHouseIdAndDeletedFalse(Long userId, Long tinyHouseId);
}
