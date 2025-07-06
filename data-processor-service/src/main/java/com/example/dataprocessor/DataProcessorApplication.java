package com.example.dataprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {KafkaAutoConfiguration.class})
@EnableDiscoveryClient
@EnableScheduling
public class DataProcessorApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataProcessorApplication.class, args);
    }
} 