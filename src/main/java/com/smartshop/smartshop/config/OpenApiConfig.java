package com.smartshop.smartshop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SmartShop Commercial Management API")
                        .description("REST API for managing products, orders, clients, and payments with loyalty program support")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Mohammed Shamkhi")
                                .email("theshamkhi1@gmail.com"))
                );
    }
}