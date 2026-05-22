package com.tinyhouse.mapper;
import com.tinyhouse.dto.response.TinyHouseResponse; import com.tinyhouse.dto.response.TinyHouseImageResponse; import com.tinyhouse.entity.TinyHouse; import com.tinyhouse.entity.TinyHouseImage;
import org.mapstruct.*;
@Mapper(componentModel = "spring")
public interface TinyHouseMapper {
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerName", expression = "java(house.getOwner().getFirstName() + \" \" + house.getOwner().getLastName())")
    @Mapping(target = "coverImageUrl", expression = "java(house.getImages() != null ? house.getImages().stream().filter(i -> i.isCoverImage()).map(i -> i.getImageUrl()).findFirst().orElse(null) : null)")
    @Mapping(target = "reviewCount", expression = "java(safeReviewCount(house))")
    TinyHouseResponse toResponse(TinyHouse house);
    TinyHouseImageResponse toImageResponse(TinyHouseImage image);

    default int safeReviewCount(TinyHouse house) {
        try {
            return house.getReviews() != null ? (int) house.getReviews().stream().filter(r -> !r.isDeleted()).count() : 0;
        } catch (org.hibernate.LazyInitializationException e) {
            return 0;
        }
    }
}
