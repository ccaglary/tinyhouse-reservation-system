package com.tinyhouse.dto.request;
import jakarta.validation.constraints.*; import lombok.*; import java.math.BigDecimal;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TinyHouseRequest {
    @NotBlank @Size(max=150) private String title; @NotBlank private String description; @NotBlank private String city;
    private String district; @NotBlank private String address; private Double latitude; private Double longitude;
    @NotNull @DecimalMin("0.01") private BigDecimal nightlyPrice; @DecimalMin("0.00") private BigDecimal cleaningFee;
    @NotNull @Min(1) private Integer capacity; @Min(1) private Integer roomCount; @Min(1) private Integer bedCount; @Min(1) private Integer bathroomCount;
    private boolean wifi; private boolean parking; private boolean airConditioner; private boolean petAllowed;
}
