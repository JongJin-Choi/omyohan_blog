package com.pilot.omyohan_blog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Omyohan Blog API")
                        .description("Admin and user APIs for the Omyohan blog service.")
                        .version("v1")
                        .contact(new Contact()
                                .name("Omyohan Blog")
                                .email("admin@example.com"))
                        .license(new License()
                                .name("Internal Use")));
    }
}
