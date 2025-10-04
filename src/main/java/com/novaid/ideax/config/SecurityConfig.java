package com.novaid.ideax.config;

import com.novaid.ideax.repository.auth.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final Filter filter; // 🔹 Lớp JWT Filter của bạn (đã có sẵn)

    // --------------------------
    // 1️⃣ Password encoder
    // --------------------------
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // --------------------------
    // 2️⃣ UserDetailsService — lấy Account từ DB
    // --------------------------
    @Bean
    public UserDetailsService userDetailsService(AccountRepository accountRepository) {
        return username -> accountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    // --------------------------
    // 3️⃣ AuthenticationProvider — dùng Account làm principal
    // --------------------------
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    // --------------------------
    // 4️⃣ AuthenticationManager — phục vụ login
    // --------------------------
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // --------------------------
    // 5️⃣ SecurityFilterChain — cấu hình filter + route
    // --------------------------
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   DaoAuthenticationProvider authenticationProvider) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .authenticationProvider(authenticationProvider) // 💡 Đăng ký provider
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT không dùng session
                .authorizeHttpRequests(req -> req
                        .requestMatchers(
                                "/auth/**",              // Cho phép login/register
                                "/uploads/**",           // Cho phép truy cập file tĩnh
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/ws/**"                 // WebSocket endpoint
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class) // Thêm JWT filter
                .build();
    }
}
