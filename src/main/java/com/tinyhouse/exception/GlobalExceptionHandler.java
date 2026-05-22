package com.tinyhouse.exception;
import com.tinyhouse.dto.response.ApiResponse; import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*; import org.springframework.security.access.AccessDeniedException; import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestControllerAdvice @Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class) public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) { log.warn("Not found: {}", ex.getMessage()); return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage())); }
    @ExceptionHandler(BusinessException.class) public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) { log.warn("Business: {}", ex.getMessage()); return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage())); }
    @ExceptionHandler(UnauthorizedException.class) public ResponseEntity<ApiResponse<Void>> handleUnauth(UnauthorizedException ex) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(ex.getMessage())); }
    @ExceptionHandler(BadCredentialsException.class) public ResponseEntity<ApiResponse<Void>> handleBadCreds(BadCredentialsException ex) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("E-posta veya şifre hatalı")); }
    @ExceptionHandler(AccessDeniedException.class) public ResponseEntity<ApiResponse<Void>> handleAccess(AccessDeniedException ex) { return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Yetkiniz yok")); }
    @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<ApiResponse<Map<String,String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String,String> errors = new HashMap<>(); ex.getBindingResult().getAllErrors().forEach(e -> errors.put(((FieldError)e).getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(ApiResponse.<Map<String,String>>builder().success(false).message("Doğrulama hatası").data(errors).build()); }
    @ExceptionHandler(Exception.class) public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) { log.error("Error", ex); return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Beklenmeyen hata")); }
}
