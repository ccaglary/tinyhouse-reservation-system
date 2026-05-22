package com.tinyhouse.mapper;
import com.tinyhouse.dto.response.NotificationResponse; import com.tinyhouse.entity.Notification; import org.mapstruct.*;
@Mapper(componentModel = "spring")
public interface NotificationMapper { NotificationResponse toResponse(Notification notification); }
