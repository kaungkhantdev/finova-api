package com.financial.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${fastforex.api.key}")
    private String apiKey;

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder
                .baseUrl("https://api.fastforex.io")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .requestInterceptor((request, body, execution) -> {
                    // Add API key to each request
                    request.getHeaders().add("X-API-Key", apiKey);
                    return execution.execute(request, body);
                })
                .build();
    }
}