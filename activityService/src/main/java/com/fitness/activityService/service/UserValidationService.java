package com.fitness.activityService.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService {
    private final WebClient userServiceWebClient;

    public boolean validateUser(String userId) {
        try {
            Boolean isValid = userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block(); // Safe to block here ONLY because logs prove you are in Spring MVC

            return Optional.ofNullable(isValid).orElse(false);
        } catch (WebClientRequestException e) {
            // Specifically catches DNS lookup failures (NXDOMAIN) and connection timeouts
            log.error("Network layer failure. Target host could not be resolved or reached: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            // Catches any other runtime errors or HTTP status exceptions
            log.error("Unexpected error validating user ID: {}", userId, e);
            return false;
        }
    }
}
