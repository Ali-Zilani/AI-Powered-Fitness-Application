package com.fitness.gateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final WebClient userServiceWebClient;

    public Mono<Boolean> validateUser(String userId) {
        log.info("Calling User Validation API for userId: {}", userId);
        return userServiceWebClient.get()                                       // ✅ return the Mono
                .uri("/api/users/{userId}/validate", userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND)             // ✅ object comparison
                        return Mono.error(new RuntimeException("User not found: " + userId));
                    else if (e.getStatusCode() == HttpStatus.BAD_REQUEST)      // ✅ object comparison
                        return Mono.error(new RuntimeException("Invalid: " + userId));
                    return Mono.error(new RuntimeException("Unexpected error: " + e.getMessage()));
                });
    }

    public Mono<UserResponse> registUser(RegisterRequest registerRequest) {
        log.info("Calling User registration for: {}", registerRequest.getEmail()); // ✅ {} placeholder
        return userServiceWebClient.post()                                      // ✅ return the Mono
                .uri("/api/users/register")
                .bodyValue(registerRequest)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                        return Mono.error(new RuntimeException("Bad Request: " + e.getMessage())); // ✅ string concat
                    return Mono.error(new RuntimeException("Unexpected error: " + e.getMessage()));
                });
    }
}