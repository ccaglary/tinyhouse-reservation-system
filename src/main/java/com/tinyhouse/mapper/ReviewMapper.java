package com.tinyhouse.mapper;
import com.tinyhouse.dto.response.ReviewResponse; import com.tinyhouse.entity.Review; import org.mapstruct.*;
@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "tinyHouseId", source = "tinyHouse.id")
    @Mapping(target = "tinyHouseTitle", source = "tinyHouse.title")
    @Mapping(target = "tenantId", source = "tenant.id")
    @Mapping(target = "tenantName", expression = "java(review.getTenant().getFirstName() + \" \" + review.getTenant().getLastName())")
    @Mapping(target = "tenantProfileImage", source = "tenant.profileImage")
    ReviewResponse toResponse(Review review);
}
