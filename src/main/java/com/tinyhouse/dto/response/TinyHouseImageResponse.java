package com.tinyhouse.dto.response;
import lombok.*; @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TinyHouseImageResponse { private Long id; private String imageUrl; private boolean coverImage; }
