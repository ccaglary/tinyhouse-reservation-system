package com.tinyhouse.repository;
import com.tinyhouse.entity.Payment; import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReservationId(Long reservationId);
    Page<Payment> findByDeletedFalse(Pageable pageable);
    Page<Payment> findByReservationTenantIdAndDeletedFalse(Long tenantId, Pageable pageable);
}
