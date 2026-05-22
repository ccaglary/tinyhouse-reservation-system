package com.tinyhouse.mapper;
import com.tinyhouse.dto.response.ReservationResponse; import com.tinyhouse.entity.Reservation; import org.mapstruct.*;
@Mapper(componentModel = "spring")
public interface ReservationMapper {
    @Mapping(target = "tenantId", source = "tenant.id")
    @Mapping(target = "tenantName", expression = "java(res.getTenant().getFirstName() + \" \" + res.getTenant().getLastName())")
    @Mapping(target = "tinyHouseId", source = "tinyHouse.id")
    @Mapping(target = "tinyHouseTitle", source = "tinyHouse.title")
    @Mapping(target = "tinyHouseCity", source = "tinyHouse.city")
    @Mapping(target = "tinyHouseCoverImage", expression = "java(res.getTinyHouse().getImages().stream().filter(i -> i.isCoverImage()).map(i -> i.getImageUrl()).findFirst().orElse(null))")
    ReservationResponse toResponse(Reservation res);
}
