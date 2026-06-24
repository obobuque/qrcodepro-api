package com.qrpro.infrastructure.security;
import com.qrpro.application.service.ApiKeyService;
import com.qrpro.domain.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    private final ApiKeyService apiKeyService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<User> userOpt = apiKeyService.authenticate(apiKey);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.id().toString(), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug("API Key authenticated: user={}", user.username());
            }
        }
        chain.doFilter(request, response);
    }
}
