package com.tinyhouse.controller.api;

import com.tinyhouse.dto.request.TinyHouseRequest;
import com.tinyhouse.dto.response.ApiResponse;
import com.tinyhouse.dto.response.TinyHouseResponse;
import com.tinyhouse.service.TinyHouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tinyhouses")
@RequiredArgsConstructor
@Tag(name = "Tiny Houses", description = "Tiny house yönetimi")
public class TinyHouseApiController {

    private final TinyHouseService tinyHouseService;

    @GetMapping
    @Operation(summary = "Aktif tiny house'ları listele")
    public ResponseEntity<ApiResponse<Page<TinyHouseResponse>>> getAll(@PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(tinyHouseService.getAllActive(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Tiny house detayı")
    public ResponseEntity<ApiResponse<TinyHouseResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(tinyHouseService.getById(id)));
    }

    @GetMapping("/search")
    @Operation(summary = "Tiny house arama ve filtreleme")
    public ResponseEntity<ApiResponse<Page<TinyHouseResponse>>> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer capacity,
            @RequestParam(required = false) Boolean wifi,
            @RequestParam(required = false) Boolean parking,
            @RequestParam(required = false) Boolean petAllowed,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                tinyHouseService.search(city, minPrice, maxPrice, capacity, wifi, parking, petAllowed, pageable)));
    }

    @GetMapping("/search/keyword")
    @Operation(summary = "Anahtar kelime ile arama")
    public ResponseEntity<ApiResponse<Page<TinyHouseResponse>>> searchByKeyword(
            @RequestParam String q, @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(tinyHouseService.searchByKeyword(q, pageable)));
    }

    @GetMapping("/cities")
    @Operation(summary = "Aktif şehirleri listele")
    public ResponseEntity<ApiResponse<List<String>>> getCities() {
        return ResponseEntity.ok(ApiResponse.success(tinyHouseService.getAllCities()));
    }

    @PostMapping
    @Operation(summary = "Yeni tiny house ekle")
    public ResponseEntity<ApiResponse<TinyHouseResponse>> create(
            @Valid @RequestBody TinyHouseRequest request, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Tiny house oluşturuldu",
                tinyHouseService.create(request, principal.getName())));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Tiny house güncelle")
    public ResponseEntity<ApiResponse<TinyHouseResponse>> update(
            @PathVariable Long id, @Valid @RequestBody TinyHouseRequest request, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(tinyHouseService.update(id, request, principal.getName())));
    }

    @PostMapping("/{id}/images")
    @Operation(summary = "Tiny house'a fotoğraf yükle")
    public ResponseEntity<ApiResponse<Void>> uploadImages(
            @PathVariable Long id, @RequestParam("files") MultipartFile[] files, Principal principal) {
        tinyHouseService.uploadImages(id, files, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Fotoğraflar yüklendi", null));
    }

    @DeleteMapping("/images/{imageId}")
    @Operation(summary = "Fotoğraf sil")
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            @PathVariable Long imageId, Principal principal) {
        tinyHouseService.deleteImage(imageId, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Fotoğraf silindi", null));
    }
}
