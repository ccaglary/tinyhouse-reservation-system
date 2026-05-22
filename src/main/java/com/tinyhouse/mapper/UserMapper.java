package com.tinyhouse.mapper;
import com.tinyhouse.dto.response.UserResponse; import com.tinyhouse.entity.User; import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface UserMapper { UserResponse toResponse(User user); }
