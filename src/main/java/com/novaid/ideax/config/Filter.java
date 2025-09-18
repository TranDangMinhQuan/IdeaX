package com.novaid.ideax.config;

import com.novaid.ideax.entity.auth.Account;
import com.novaid.ideax.exception.AuthenticationException;
import com.novaid.ideax.repository.auth.AuthenticationRepository;
import com.novaid.ideax.service.auth.impl.TokenServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class Filter extends OncePerRequestFilter {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Autowired
    private TokenServiceImpl tokenService;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    // Các API public (không cần token)
    private final List<String> PUBLIC_API = List.of(
            "POST:/auth/register/startup",
            "POST:/auth/register/investor",
            "POST:/auth/login",
            "GET:/swagger-ui/**",
            "GET:/v3/api-docs/**",
            "GET:/swagger-resources/**",
            "GET:/webjars/**",
            "GET:/swagger-ui.html"
    );

    private boolean isPublicAPI(String uri, String method) {
        AntPathMatcher matcher = new AntPathMatcher();
        return PUBLIC_API.stream().anyMatch(pattern -> {
            String[] parts = pattern.split(":", 2);
            if (parts.length != 2) return false;
            String allowedMethod = parts[0];
            String allowedUri = parts[1];
            return method.equalsIgnoreCase(allowedMethod) && matcher.match(allowedUri, uri);
        });
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // Nếu API public thì bỏ qua filter
        if (isPublicAPI(uri, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Lấy token
        String token = getToken(request);
        if (token == null) {
            resolver.resolveException(request, response, null,
                    new AuthenticationException("Empty token!"));
            return;
        }

        Account account;
        try {
            account = tokenService.extractAccount(token);
        } catch (ExpiredJwtException e) {
            resolver.resolveException(request, response, null,
                    new AuthenticationException("Expired Token!"));
            return;
        } catch (MalformedJwtException e) {
            resolver.resolveException(request, response, null,
                    new AuthenticationException("Invalid Token!"));
            return;
        }

        if (account != null) {
            // ⚡ Convert Account -> UserDetails thay vì nhét thẳng Account
            UserDetails userDetails = User.withUsername(account.getEmail())
                    .password(account.getPassword()) // password encode
                    .authorities("ROLE_"+account.getRole().name())
                    .build();

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            token,
                            userDetails.getAuthorities()
                    );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }
}
