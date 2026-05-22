package com.tinyhouse.controller.api;

import com.tinyhouse.dto.request.ReviewRequest;
import com.tinyhouse.dto.response.ApiResponse;
import com.tinyhouse.dto.response.ReviewResponse;
import com.tinyhouse.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Yorum yönetimi")
public class ReviewApiController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Yorum yap")
    public ResponseEntity<ApiResponse<ReviewResponse>> create(
            @Valid @RequestBody ReviewRequest request, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.create(request, principal.getName())));
    }

    @GetMapping("/tinyhouse/{tinyHouseId}")
    @Operation(summary = "Tiny house yorumları")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getByTinyHouse(
            @PathVariable Long tinyHouseId, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getByTinyHouse(tinyHouseId, pageable)));
    }

    @PutMapping("/{id}/reply")
    @Operation(summary = "Yoruma yanıt ver")
    public ResponseEntity<ApiResponse<ReviewResponse>> reply(
            @PathVariable Long id, @RequestParam String reply, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.replyToReview(id, reply, principal.getName())));
    }
}
