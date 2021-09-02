package com.gateway.greens;

import com.gateway.code.GreensType;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class GreensServiceFactory {
    private final Map<GreensType, GreensService> clientMap;

    public GreensServiceFactory(Collection<GreensService> clients) {
        clientMap = clients.stream()
                .collect(Collectors.toMap(GreensService::getType, client -> client));
    }

    public GreensService getClient(GreensType type) {
        return clientMap.get(type);
    }
}
