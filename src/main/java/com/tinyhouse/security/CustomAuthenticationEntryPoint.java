package com.tinyhouse.security;
import com.fasterxml.jackson.databind.ObjectMapper; import com.tinyhouse.dto.response.ApiResponse;
import jakarta.servlet.http.*; import org.springframework.http.MediaType; import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint; import org.springframework.stereotype.Component; import java.io.IOException;
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException e) throws IOException {
        if (req.getRequestURI().startsWith("/api/")) { res.setContentType(MediaType.APPLICATION_JSON_VALUE); res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            new ObjectMapper().writeValue(res.getOutputStream(), ApiResponse.error("Yetkilendirme gerekli")); }
        else res.sendRedirect("/login");
    }
}
