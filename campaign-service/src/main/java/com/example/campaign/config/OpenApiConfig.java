package com.example.campaign.config;

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
    public OpenAPI campaignServiceOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8081");
        localServer.setDescription("Campaign Service - Local Environment");

        Contact contact = new Contact();
        contact.setName("Campaign Service Team");
        contact.setEmail("campaign@example.com");
        contact.setUrl("https://www.example.com");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Campaign Service API")
                .version("1.0")
                .contact(contact)
                .description("광고 캠페인 관리를 위한 REST API 서비스입니다. 캠페인 생성, 조회, 상태 변경, 예산 관리 등의 기능을 제공합니다.")
                .termsOfService("https://www.example.com/terms")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
} 