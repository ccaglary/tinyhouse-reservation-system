package com.tinyhouse.controller.api;

import com.tinyhouse.dto.response.ApiResponse;
import com.tinyhouse.dto.response.TinyHouseResponse;
import com.tinyhouse.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorites", description = "Favori yönetimi")
public class FavoriteApiController {

    private final FavoriteService favoriteService;

    @PostMapping("/{tinyHouseId}")
    @Operation(summary = "Favorilere ekle/çıkar")
    public ResponseEntity<ApiResponse<Boolean>> toggle(
            @PathVariable Long tinyHouseId, Principal principal) {
        boolean added = favoriteService.toggleFavorite(principal.getName(), tinyHouseId);
        String msg = added ? "Favorilere eklendi" : "Favorilerden çıkarıldı";
        return ResponseEntity.ok(ApiResponse.success(msg, added));
    }

    @GetMapping
    @Operation(summary = "Favorilerimi listele")
    public ResponseEntity<ApiResponse<Page<TinyHouseResponse>>> getMyFavorites(
            Principal principal, @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(favoriteService.getUserFavorites(principal.getName(), pageable)));
    }

    @GetMapping("/check/{tinyHouseId}")
    @Operation(summary = "Favori kontrolü")
    public ResponseEntity<ApiResponse<Boolean>> check(
            @PathVariable Long tinyHouseId, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(favoriteService.isFavorite(principal.getName(), tinyHouseId)));
    }
}
