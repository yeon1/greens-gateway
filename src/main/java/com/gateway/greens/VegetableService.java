package com.gateway.greens;

import com.gateway.config.GreensServiceProperties;
import com.gateway.code.GreensType;
import org.springframework.web.client.RestTemplate;

public class VegetableService extends GreensService {
    public VegetableService(GreensServiceProperties properties, RestTemplate restTemplate) {
        super(properties, restTemplate);
    }

    @Override
    public GreensType getType() {
        return GreensType.VEGETABLE;
    }
}
