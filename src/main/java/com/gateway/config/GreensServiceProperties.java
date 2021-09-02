package com.gateway.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConstructorBinding;

@RequiredArgsConstructor
@Getter
@ConstructorBinding
public class GreensServiceProperties {
    private final String tokenUrl;
    private final String getUrl;
}
