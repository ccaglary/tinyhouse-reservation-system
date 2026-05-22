package com.tinyhouse.repository;
import com.tinyhouse.entity.TinyHouseImage; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface TinyHouseImageRepository extends JpaRepository<TinyHouseImage, Long> {
    List<TinyHouseImage> findByTinyHouseIdAndDeletedFalse(Long tinyHouseId);
}
