package com.fitness.gateway;

import com.fitness.gateway.user.RegisterRequest;
import com.fitness.gateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest; // ✅ Fixed import
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeyCloakUserSyncFilter implements WebFilter {

    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        RegisterRequest registerRequest = getUserDetails(token);

        if (userId == null && registerRequest != null) {
            userId = registerRequest.getKeyCloakId();
        }

        if (userId != null && token != null) {
            String finalUserId = userId; // ✅ Effectively final for lambda
            return userService.validateUser(userId).flatMap(exist -> {
                if (!exist) {
                    if (registerRequest != null) {
                        return userService.registUser(registerRequest)
                                .then(Mono.empty());
                    } else {
                        return Mono.empty();
                    }
                } else {
                    log.info("User already exist, Skipping sync");
                    return Mono.empty();
                }
            }).then(Mono.defer(() -> {
                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                        .header("X-User-ID", finalUserId)
                        .build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            }));
        }

        return chain.filter(exchange); // ✅ Never return null from a Mono method
    }

    private RegisterRequest getUserDetails(String token) {
        try {
            String tokenWithoutBearer = token.replace("Bearer ", "").trim(); // ✅ Space included
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            RegisterRequest request = new RegisterRequest();
            request.setEmail(claims.getStringClaim("email"));
            request.setKeyCloakId(claims.getStringClaim("sub"));
            request.setFirstName(claims.getStringClaim("given_name"));
            request.setLastName(claims.getClaimAsString("family_name"));
            request.setPassword("dummy@123");
            return request;
        } catch (Exception e) { // ✅ Catch broadly so null can legitimately be returned
            e.printStackTrace();
            return null;
        }
    }
}