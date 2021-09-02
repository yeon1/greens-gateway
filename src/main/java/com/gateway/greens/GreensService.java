package com.gateway.greens;

import com.gateway.code.GreensType;
import com.gateway.common.Product;
import com.gateway.common.AccessToken;
import com.gateway.config.GreensServiceProperties;
import com.gateway.exception.GreensClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class GreensService {
    private final GreensServiceProperties properties;
    private final RestTemplate restTemplate;

    public abstract GreensType getType();

    protected GreensServiceProperties getProperties() {
        return properties;
    }

    public List<String> getList() {
        String token = getToken();
        return getList(token);
    }

    public Product get(String name) {
        String token = getToken();

        return get(token, name);
    }

    private <T> T requestGet(URI uri, ParameterizedTypeReference<T> typeReference) {
        return requestGet(uri, null, typeReference);
    }

    private <T> T requestGet(String url, ParameterizedTypeReference<T> typeReference) {
        return requestGet(url, null, typeReference);
    }

    private <T> T requestGet(String url, String token, ParameterizedTypeReference<T> typeReference) {
        URI uri;

        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new GreensClientException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Invalid request url %s.", url), e.getMessage());
        }

        return requestGet(uri, token, typeReference);
    }

    private <T> T requestGet(URI uri, String token, ParameterizedTypeReference<T> typeReference) {
        HttpHeaders headers = new HttpHeaders();

        if (StringUtils.hasLength(token)) headers.set(HttpHeaders.AUTHORIZATION, token);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        T response;

        try {
            ResponseEntity<T> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, typeReference);
            response = responseEntity.getBody();
        } catch (HttpStatusCodeException e) {
            throw new GreensClientException(e.getStatusCode(), String.format("%s api(%s) request fail. %s", getType(), uri, e.getResponseBodyAsString()));
        } catch (RestClientException e) {
            throw new GreensClientException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("%s api(%s) request fail.", getType(), uri), e.getMessage());
        }

        return response;
    }

    protected AccessToken getAccessToken() {
        GreensServiceProperties properties = getProperties();
        ParameterizedTypeReference<AccessToken> typeReference = new ParameterizedTypeReference<>() {};
        return requestGet(properties.getTokenUrl(), typeReference);
    }

    private String getToken() {
        ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession httpSession = servletRequestAttribute.getRequest().getSession(true);
        String attributeName = String.format("%s_TOKEN", getType());
        Object token = httpSession.getAttribute(attributeName);

        if (token != null) return token.toString();

        AccessToken accessToken = getAccessToken();
        httpSession.setAttribute(attributeName, accessToken.getAccessToken());

        return accessToken.getAccessToken();
    }

    private List<String> getList(String token) {
        GreensServiceProperties properties = getProperties();
        ParameterizedTypeReference<List<String>> typeReference = new ParameterizedTypeReference<>() {};
        return requestGet(properties.getGetUrl(), token, typeReference);
    }

    private Product get(String token, String name) {
        GreensServiceProperties properties = getProperties();
        URI uri;

        try {
            uri = new URIBuilder(properties.getGetUrl())
                    .addParameter("name", name)
                    .build();
        } catch (URISyntaxException e) {
            throw new GreensClientException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Invalid request url %s.", properties.getGetUrl()), e.getMessage());
        }

        ParameterizedTypeReference<Product> typeReference = new ParameterizedTypeReference<>() {};
        return requestGet(uri, token, typeReference);
    }

}
