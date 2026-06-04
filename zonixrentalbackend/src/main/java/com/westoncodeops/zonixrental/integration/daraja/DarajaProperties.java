package com.westoncodeops.zonixrental.integration.daraja;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "daraja")
public class DarajaProperties {
    private String consumerKey;
    private String consumerSecret;
    private String shortCode;
    private String passKey;
    private String callbackUrl;
    private String environment = "sandbox";
    private String businessShortCode;
}
