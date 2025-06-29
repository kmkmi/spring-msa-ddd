package com.example.publisher.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI publisherServiceOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8084");
        localServer.setDescription("Publisher Service - Local Environment");

        Contact contact = new Contact();
        contact.setName("Publisher Service Team");
        contact.setEmail("publisher@example.com");
        contact.setUrl("https://www.example.com");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Publisher Service API")
                .version("1.0")
                .contact(contact)
                .description("퍼블리셔 관리를 위한 REST API 서비스입니다. 퍼블리셔 등록, 조회, 정보 수정 등의 기능을 제공합니다.")
                .termsOfService("https://www.example.com/terms")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
} 