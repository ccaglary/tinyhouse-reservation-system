package com.tinyhouse.dto.request;
import jakarta.validation.constraints.*; import lombok.*; import java.time.LocalDate;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReservationRequest {
    @NotNull private Long tinyHouseId; @NotNull @FutureOrPresent private LocalDate startDate;
    @NotNull @Future private LocalDate endDate; private String specialRequest;
}
