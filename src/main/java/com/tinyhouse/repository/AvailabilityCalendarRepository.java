package com.tinyhouse.repository;
import com.tinyhouse.entity.AvailabilityCalendar; import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate; import java.util.List;
public interface AvailabilityCalendarRepository extends JpaRepository<AvailabilityCalendar, Long> {
    List<AvailabilityCalendar> findByTinyHouseIdAndAvailableDateBetween(Long tinyHouseId, LocalDate start, LocalDate end);
}
