package com.tinyhouse.repository;
import com.tinyhouse.entity.User; import com.tinyhouse.enums.Role; import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.data.jpa.repository.Query; import org.springframework.data.repository.query.Param; import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndDeletedFalse(String email);
    Optional<User> findByVerificationTokenAndDeletedFalse(String token);
    Optional<User> findByResetTokenAndDeletedFalse(String token);
    Optional<User> findByRefreshTokenAndDeletedFalse(String token);
    boolean existsByEmail(String email);
    long countByDeletedFalse();
    long countByRole(Role role);
    Page<User> findByDeletedFalse(Pageable pageable);
    Page<User> findByRoleAndDeletedFalse(Role role, Pageable pageable);
    @Query("SELECT u FROM User u WHERE u.deleted = false AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%',:q,'%')))")
    Page<User> searchUsers(@Param("q") String query, Pageable pageable);
}
