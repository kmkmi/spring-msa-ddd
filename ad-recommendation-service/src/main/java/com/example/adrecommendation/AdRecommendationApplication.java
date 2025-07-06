package com.example.adrecommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AdRecommendationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdRecommendationApplication.class, args);
    }
} 