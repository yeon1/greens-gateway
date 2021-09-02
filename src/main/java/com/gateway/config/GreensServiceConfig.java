package com.gateway.config;

import com.gateway.greens.FruitService;
import com.gateway.greens.GreensService;
import com.gateway.greens.GreensServiceFactory;
import com.gateway.greens.VegetableService;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties("greens")
@Configuration
public class GreensServiceConfig {

    @Getter
    private final Map<String, GreensServiceProperties> clients = new HashMap<>();

    @Bean
    public GreensServiceFactory greensClientFactory(ApplicationContext applicationContext) {
        Map<String, GreensService> beans = applicationContext.getBeansOfType(GreensService.class);

        return new GreensServiceFactory(beans.values());
    }

    @Bean
    public FruitService fruitClient(RestTemplate restTemplate) {
        return new FruitService(clients.get("fruit"), restTemplate);
    }

    @Bean
    public VegetableService vegetableClient(RestTemplate restTemplate) {
        return new VegetableService(clients.get("vegetable"), restTemplate);
    }
}
