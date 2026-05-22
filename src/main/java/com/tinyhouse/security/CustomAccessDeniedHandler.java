package com.tinyhouse.security;
import com.fasterxml.jackson.databind.ObjectMapper; import com.tinyhouse.dto.response.ApiResponse;
import jakarta.servlet.http.*; import org.springframework.http.MediaType; import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler; import org.springframework.stereotype.Component; import java.io.IOException;
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException e) throws IOException {
        if (req.getRequestURI().startsWith("/api/")) { res.setContentType(MediaType.APPLICATION_JSON_VALUE); res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            new ObjectMapper().writeValue(res.getOutputStream(), ApiResponse.error("Yetkiniz yok")); }
        else res.sendRedirect("/access-denied");
    }
}
