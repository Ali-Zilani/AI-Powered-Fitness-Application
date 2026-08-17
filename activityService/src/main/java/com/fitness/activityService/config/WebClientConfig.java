package com.fitness.activityService.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient userServiceWebClient(WebClient.Builder webClientBuilder) {
        // You MUST use the passed-in 'webClientBuilder' parameter instance.
        // This ensures the @LoadBalanced configurations are applied to it.
        return webClientBuilder
                .baseUrl("http://USER-SERVICE")
                .build();
    }
}
