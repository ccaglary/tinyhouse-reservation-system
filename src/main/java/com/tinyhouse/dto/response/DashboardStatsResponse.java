package com.tinyhouse.dto.response;
import lombok.*; import java.math.BigDecimal;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardStatsResponse { private long totalUsers; private long totalOwners; private long totalTenants; private long totalTinyHouses; private long activeTinyHouses; private long totalReservations; private long activeReservations; private long pendingReservations; private BigDecimal totalRevenue; private BigDecimal monthlyRevenue; private long totalReviews; }
