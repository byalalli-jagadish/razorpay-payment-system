package com.razorpay.merchant.security;

import com.razorpay.merchant.entity.ApiKey;
import com.razorpay.merchant.repository.ApikeyRepository;
import com.razorpay.merchant.cache.ApiKeyCacheEntry;
import com.razorpay.merchant.cache.ApiKeyCache;
import com.razorpay.common.ratelimit.RateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    private final ApikeyRepository apikeyRepository;
    private final MerchantContext merchantContext;
    private final ApiKeyCache apiKeyCache;
    private final RateLimiter rateLimiter;
    @Value("${app.rate-limit.use-case.api-key.requests-per-minute:200}")
    private int requestsPerMinute;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/v1/orders/")
                && !request.getRequestURI().equals("/v1/orders")
                && !request.getRequestURI().startsWith("/v1/payments/")
                && !request.getRequestURI().equals("/v1/payments")
                && !request.getRequestURI().startsWith("/v1/vault/")
                && !request.getRequestURI().equals("/v1/vault");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Basic ")) {
            unauthorized(response, "Missing API key credentials");
            return;
        }

        try {
            String decoded = new String(Base64.getDecoder().decode(
                    authorization.substring("Basic ".length())), StandardCharsets.UTF_8);
            int separator = decoded.indexOf(':');
            if (separator <= 0) {
                unauthorized(response, "Invalid API key credentials");
                return;
            }

            String keyId = decoded.substring(0, separator);
            String rawSecret = decoded.substring(separator + 1);
            ApiKeyCacheEntry cached = apiKeyCache.get(keyId).orElse(null);
            ApiKey apiKey = cached == null ? apikeyRepository.findByKeyId(keyId).orElse(null) : null;

            if (cached == null && apiKey != null) {
                cached = new ApiKeyCacheEntry(keyId, apiKey.getKeySecretHash(), apiKey.getPreviousKeySecretHash(),
                        apiKey.getGracePeriodExpiresAt(), apiKey.getMerchant().getId(), apiKey.isEnabled());
                apiKeyCache.put(cached);
            }
            if (cached == null || !cached.enabled() || !matches(rawSecret, cached)) {
                unauthorized(response, "Invalid or disabled API key");
                return;
            }

            var limit = rateLimiter.check("apikey:" + keyId, requestsPerMinute, 60);
            response.setHeader("X-RateLimit-Limit", String.valueOf(requestsPerMinute));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(limit.remaining()));
            if (!limit.allowed()) {
                response.setStatus(429);
                response.setHeader("Retry-After", String.valueOf(limit.retryAfterSeconds()));
                response.getWriter().write("{\"error\":\"Rate limit exceeded\"}");
                return;
            }

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            keyId, null, List.of(new SimpleGrantedAuthority("API_KEY"))));
            merchantContext.setMerchantId(cached.merchantId());
            merchantContext.setKeyId(keyId);
            filterChain.doFilter(request, response);
        } catch (IllegalArgumentException exception) {
            unauthorized(response, "Invalid API key credentials");
        }
    }

    private boolean matches(String rawSecret, ApiKeyCacheEntry key) {
        return passwordEncoder.matches(rawSecret, key.secretHash())
                || (key.inGracePeriod() && key.previousSecretHash() != null
                && passwordEncoder.matches(rawSecret, key.previousSecretHash()));
    }

    private boolean matchesAndMigrate(ApiKey apiKey, String rawSecret) {
        String storedSecret = apiKey.getKeySecretHash();
        if (storedSecret == null) {
            return false;
        }

        if (passwordEncoder.matches(rawSecret, storedSecret)) {
            return true;
        }

        // Local-development migration for keys created before BCrypt hashing was added.
        if (rawSecret.equals(storedSecret)) {
            apiKey.setKeySecretHash(passwordEncoder.encode(rawSecret));
            apikeyRepository.save(apiKey);
            return true;
        }

        return false;
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
