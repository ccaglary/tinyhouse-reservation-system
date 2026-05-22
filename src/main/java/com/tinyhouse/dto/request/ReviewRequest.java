package com.tinyhouse.dto.request;
import jakarta.validation.constraints.*; import lombok.*; import java.math.BigDecimal;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReviewRequest { @NotNull private Long tinyHouseId; @NotNull @DecimalMin("1.0") @DecimalMax("5.0") private BigDecimal rating; @Size(max=1000) private String comment; }
