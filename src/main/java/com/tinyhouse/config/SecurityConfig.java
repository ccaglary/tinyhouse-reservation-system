package com.tinyhouse.config;
import com.tinyhouse.jwt.JwtAuthenticationFilter; import com.tinyhouse.security.*;
import lombok.RequiredArgsConstructor; import org.springframework.context.annotation.Bean; import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager; import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain; import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration @EnableWebSecurity @EnableMethodSecurity @RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtFilter; private final CustomAuthenticationEntryPoint entryPoint; private final CustomAccessDeniedHandler accessDenied;
    @Bean public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(c -> c.disable())
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(e -> e.authenticationEntryPoint(entryPoint).accessDeniedHandler(accessDenied))
            .authorizeHttpRequests(a -> a
                .requestMatchers("/", "/home", "/login", "/register", "/forgot-password", "/reset-password", "/verify-email", "/access-denied").permitAll()
                .requestMatchers("/tinyhouses", "/tinyhouses/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/tinyhouses/**").permitAll()
                .requestMatchers("/api/reviews/tinyhouse/**").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**", "/webjars/**", "/favicon.ico").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/owner/**").hasAnyRole("OWNER", "ADMIN")
                .requestMatchers("/owner/**").hasAnyRole("OWNER", "ADMIN")
                .anyRequest().authenticated())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception { return config.getAuthenticationManager(); }
}
