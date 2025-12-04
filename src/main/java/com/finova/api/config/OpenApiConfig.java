package com.finova.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    public static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Financial Application API")
                        .version("1.0.0")
                        .description("""
                                REST API documentation with JWT Authentication.
                                
                                **Authentication Methods:**
                                - **Web (Cookie-based)**: Login via `/api/v1/auth/login` endpoint. JWT tokens are automatically stored in HTTP-only cookies.
                                - **Mobile (Bearer Token)**: Login via `/api/v1/auth/mobile/login` endpoint. Use the returned access token in the Authorization header.
                                
                                **Testing in Swagger:**
                                - For mobile endpoints: Click 'Authorize' button and enter your JWT token.
                                - For web endpoints: First call `/api/v1/auth/login`, then subsequent requests will include cookies automatically.
                                
                                Note: Cookie authentication works best when testing from the same domain.
                                """)
                        .contact(new Contact()
                                .name("Your Name")
                                .email("your.email@company.com")
                                .url("https://www.company.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.company.com")
                                .description("Production Server")
                ))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH,
                                new SecurityScheme()
                                        .name(BEARER_AUTH)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token (obtainable from /api/v1/auth/mobile/login or /api/v1/auth/login)")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }
}