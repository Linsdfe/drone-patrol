package com.drone.patrol.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "zhipu")
public class ZhipuConfig {
    private String apiKey;
    private String baseUrl;
    private String model;
}