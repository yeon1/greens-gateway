package com.gateway.greens;

import com.gateway.code.GreensType;
import com.gateway.common.AccessToken;
import com.gateway.common.Product;
import com.gateway.config.GreensServiceProperties;
import com.gateway.exception.GreensClientException;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class GreensServiceTest {

    private GreensServiceFactory serviceFactory;

    @Mock
    private RestTemplate restTemplate;

    private MockHttpServletRequest request;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        GreensServiceProperties fruitServiceProperties = new GreensServiceProperties("http://fruit.api.postype.net/token", "http://fruit.api.postype.net/product");
        GreensServiceProperties vegetableServiceProperties = new GreensServiceProperties("http://vegetable.api.postype.net/token", "http://vegetable.api.postype.net/item");

        Set<GreensService> serviceSet = new HashSet<>();
        serviceSet.add(new FruitService(fruitServiceProperties, restTemplate));
        serviceSet.add(new VegetableService(vegetableServiceProperties, restTemplate));

        serviceFactory = new GreensServiceFactory(serviceSet);
    }

    @Test
    public void ??????_??????_??????_?????????() throws URISyntaxException {
        GreensService client = serviceFactory.getClient(GreensType.VEGETABLE);

        List<String> vegetables = Arrays.asList("?????????", "?????????", "??????", "??????");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        ResponseEntity<List<String>> getResponseEntity = new ResponseEntity<>(vegetables, headers, HttpStatus.OK);

        AccessToken accessToken = new AccessToken("token");
        ResponseEntity<AccessToken> tokenResponseEntity = new ResponseEntity<>(accessToken, headers, HttpStatus.OK);

        given(restTemplate.exchange(eq(new URI("http://vegetable.api.postype.net/token")), eq(HttpMethod.GET), any(), eq(new ParameterizedTypeReference<AccessToken>() {})))
                .willReturn(tokenResponseEntity);
        given(restTemplate.exchange(eq(new URI("http://vegetable.api.postype.net/item")), eq(HttpMethod.GET), any(), eq(new ParameterizedTypeReference<List<String>>() {})))
                .willReturn(getResponseEntity);

        List<String> result = client.getList();

        assertEquals(client.getType(), GreensType.VEGETABLE);
        assertEquals(result.size(), 4);
        assertThat(result, hasItems("?????????", "?????????", "??????", "??????"));
    }


    @Test
    public void ??????_??????_?????????() throws URISyntaxException {
        GreensService client = serviceFactory.getClient(GreensType.VEGETABLE);

        Product product = new Product("??????", 10000);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        ResponseEntity<Product> getResponseEntity = new ResponseEntity<>(product, headers, HttpStatus.OK);

        AccessToken accessToken = new AccessToken("token");
        ResponseEntity<AccessToken> tokenResponseEntity = new ResponseEntity<>(accessToken, headers, HttpStatus.OK);

        given(restTemplate.exchange(
                eq(new URI("http://vegetable.api.postype.net/token")),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<AccessToken>() {})))
                .willReturn(tokenResponseEntity);
        given(restTemplate.exchange(
                eq(new URIBuilder("http://vegetable.api.postype.net/item")
                        .addParameter("name", "??????")
                        .build()),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<Product>() {})))
                .willReturn(getResponseEntity);

        Product result = client.get("??????");

        assertEquals(client.getType(), GreensType.VEGETABLE);
        assertEquals(result.getName(), "??????");
        assertEquals(result.getPrice(), 10000);
    }

    @Test
    public void ??????_??????_??????_?????????() throws URISyntaxException {
        GreensService client = serviceFactory.getClient(GreensType.VEGETABLE);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        AccessToken accessToken = new AccessToken("token");
        ResponseEntity<AccessToken> tokenResponseEntity = new ResponseEntity<>(accessToken, headers, HttpStatus.OK);

        given(restTemplate.exchange(
                eq(new URI("http://vegetable.api.postype.net/token")),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<AccessToken>() {})))
                .willReturn(tokenResponseEntity);
        given(restTemplate.exchange(
                eq(new URIBuilder("http://vegetable.api.postype.net/item")
                        .addParameter("name", "??????")
                        .build()),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<Product>() {})))
                .willThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(GreensClientException.class, () -> client.get("??????"));
    }


    @Test
    public void ??????_??????_??????_?????????() throws URISyntaxException {
        GreensService client = serviceFactory.getClient(GreensType.FRUIT);

        List<String> fruits = Arrays.asList("???", "?????????", "??????", "?????????");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        ResponseEntity<List<String>> getResponseEntity = new ResponseEntity<>(fruits, headers, HttpStatus.OK);

        AccessToken accessToken = new AccessToken("token");
        ResponseEntity<AccessToken> tokenResponseEntity = new ResponseEntity<>(accessToken, headers, HttpStatus.OK);

        given(restTemplate.exchange(eq(new URI("http://fruit.api.postype.net/token")), eq(HttpMethod.GET), any(), eq(new ParameterizedTypeReference<AccessToken>() {})))
                .willReturn(tokenResponseEntity);
        given(restTemplate.exchange(eq(new URI("http://fruit.api.postype.net/product")), eq(HttpMethod.GET), any(), eq(new ParameterizedTypeReference<List<String>>() {})))
                .willReturn(getResponseEntity);

        List<String> result = client.getList();

        assertEquals(client.getType(), GreensType.FRUIT);
        assertEquals(result.size(), 4);
        assertThat(result, hasItems("???", "?????????", "??????", "?????????"));
    }


    @Test
    public void ??????_??????_?????????() throws URISyntaxException {
        GreensService client = serviceFactory.getClient(GreensType.FRUIT);

        Product product = new Product("??????", 12000);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        ResponseEntity<Product> getResponseEntity = new ResponseEntity<>(product, headers, HttpStatus.OK);

        AccessToken accessToken = new AccessToken("token");
        ResponseEntity<AccessToken> tokenResponseEntity = new ResponseEntity<>(accessToken, headers, HttpStatus.OK);

        given(restTemplate.exchange(eq(new URI("http://fruit.api.postype.net/token")), eq(HttpMethod.GET), any(), eq(new ParameterizedTypeReference<AccessToken>() {})))
                .willReturn(tokenResponseEntity);
        given(restTemplate.exchange(
                eq(new URIBuilder("http://fruit.api.postype.net/product")
                        .addParameter("name", "??????")
                        .build()),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<Product>() {})))
                .willReturn(getResponseEntity);

        Product result = client.get("??????");

        assertEquals(client.getType(), GreensType.FRUIT);
        assertEquals(result.getName(), "??????");
        assertEquals(result.getPrice(), 12000);
    }

    @Test
    public void ??????_??????_??????_?????????() throws URISyntaxException {
        GreensService client = serviceFactory.getClient(GreensType.FRUIT);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        AccessToken accessToken = new AccessToken("token");
        ResponseEntity<AccessToken> tokenResponseEntity = new ResponseEntity<>(accessToken, headers, HttpStatus.OK);

        given(restTemplate.exchange(
                eq(new URI("http://fruit.api.postype.net/token")),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<AccessToken>() {})))
                .willReturn(tokenResponseEntity);
        given(restTemplate.exchange(
                eq(new URIBuilder("http://fruit.api.postype.net/product")
                        .addParameter("name", "?????????")
                        .build()),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<Product>() {})))
                .willThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(GreensClientException.class, () -> client.get("?????????"));
    }
}
