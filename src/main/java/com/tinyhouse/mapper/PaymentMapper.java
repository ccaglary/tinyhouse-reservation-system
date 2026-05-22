package com.tinyhouse.mapper;
import com.tinyhouse.dto.response.PaymentResponse; import com.tinyhouse.entity.Payment; import org.mapstruct.*;
@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(target = "reservationId", source = "reservation.id")
    PaymentResponse toResponse(Payment payment);
}
