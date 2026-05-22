package com.tinyhouse.controller.api;

import com.tinyhouse.dto.request.PaymentRequest;
import com.tinyhouse.dto.response.ApiResponse;
import com.tinyhouse.dto.response.PaymentResponse;
import com.tinyhouse.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Ödeme işlemleri")
public class PaymentApiController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Ödeme yap")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @Valid @RequestBody PaymentRequest request, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Ödeme işlendi",
                paymentService.processPayment(request, principal.getName())));
    }

    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "Rezervasyon ödemesini görüntüle")
    public ResponseEntity<ApiResponse<PaymentResponse>> getByReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getByReservation(reservationId)));
    }
}
