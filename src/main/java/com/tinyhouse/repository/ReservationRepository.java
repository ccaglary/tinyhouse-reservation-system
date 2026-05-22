package com.tinyhouse.repository;
import com.tinyhouse.entity.Reservation; import com.tinyhouse.enums.ReservationStatus; import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.data.jpa.repository.Query; import org.springframework.data.repository.query.Param;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List;
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Page<Reservation> findByTenantIdAndDeletedFalse(Long tenantId, Pageable pageable);
    Page<Reservation> findByTinyHouseOwnerIdAndDeletedFalse(Long ownerId, Pageable pageable);
    Page<Reservation> findByDeletedFalse(Pageable pageable);
    Page<Reservation> findByReservationStatusAndDeletedFalse(ReservationStatus status, Pageable pageable);
    long countByDeletedFalse();
    long countByReservationStatusAndDeletedFalse(ReservationStatus status);
    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.tinyHouse.id = :houseId AND r.deleted = false AND r.reservationStatus NOT IN ('CANCELLED','REJECTED') AND r.startDate <= :endDate AND r.endDate >= :startDate")
    boolean existsConflict(@Param("houseId") Long houseId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    @Query("SELECT COALESCE(SUM(r.totalPrice), 0) FROM Reservation r WHERE r.reservationStatus = 'COMPLETED' AND r.deleted = false")
    BigDecimal calculateTotalRevenue();
    @Query("SELECT COALESCE(SUM(r.totalPrice), 0) FROM Reservation r WHERE r.reservationStatus = 'COMPLETED' AND r.deleted = false AND r.createdAt >= :from")
    BigDecimal calculateRevenueFrom(@Param("from") LocalDateTime from);
    @Query("SELECT COALESCE(SUM(r.totalPrice), 0) FROM Reservation r WHERE r.tinyHouse.owner.id = :ownerId AND r.reservationStatus = 'COMPLETED' AND r.deleted = false")
    BigDecimal calculateOwnerRevenue(@Param("ownerId") Long ownerId);
    @Query("SELECT FUNCTION('DATE', r.createdAt), COUNT(r), COALESCE(SUM(r.totalPrice),0) FROM Reservation r WHERE r.deleted = false AND r.createdAt >= :from GROUP BY FUNCTION('DATE', r.createdAt) ORDER BY FUNCTION('DATE', r.createdAt)")
    List<Object[]> getDailyStats(@Param("from") LocalDateTime from);
    @Query("SELECT r.tinyHouse.id, r.tinyHouse.title, COUNT(r) FROM Reservation r WHERE r.deleted = false AND r.reservationStatus NOT IN ('CANCELLED','REJECTED') GROUP BY r.tinyHouse.id, r.tinyHouse.title ORDER BY COUNT(r) DESC")
    List<Object[]> getMostReservedHouses(Pageable pageable);
}
