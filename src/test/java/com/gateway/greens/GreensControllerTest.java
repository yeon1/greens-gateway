package com.gateway.greens;


import com.gateway.common.AccessToken;
import com.gateway.common.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.*;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class GreensControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext ctx;

    @MockBean
    private RestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();
    }

    @Test
    public void 채소_목록_조회_테스트() throws Exception {
        List<String> vegetables = Arrays.asList("치커리", "토마토", "깻잎", "상추");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        ResponseEntity<List<String>> getResponseEntity = new ResponseEntity<>(vegetables, headers, HttpStatus.OK);

        AccessToken accessToken = new AccessToken("token");
        ResponseEntity<AccessToken> tokenResponseEntity = new ResponseEntity<>(accessToken, headers, HttpStatus.OK);

        given(restTemplate.exchange(
                eq(new URI("http://vegetable.api.postype.net/token")),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<AccessToken>() {})))
                .willReturn(tokenResponseEntity);
        given(restTemplate.exchange(
                eq(new URI("http://vegetable.api.postype.net/item")),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<List<String>>() {})))
                .willReturn(getResponseEntity);

        mockMvc.perform(get("/greens?type=VEGETABLE"))
                .andExpect(status().isOk());
    }

    @Test
    public void 채소_조회_테스트() throws Exception {
        Product product = new Product("토마토", 3000);
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
                        .addParameter("name", "토마토")
                        .build()),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<Product>() {})))
                .willReturn(getResponseEntity);

        mockMvc.perform(get("/greens/VEGETABLE?name=토마토"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("토마토")))
                .andExpect(jsonPath("$.price", is(3000)));
    }

    @Test
    public void 채소_조회_실패_테스트() throws Exception {
        Product product = new Product("가지", 3000);
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
                        .addParameter("name", "가지")
                        .build()),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<Product>() {})))
                .willThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/greens/VEGETABLE?name=가지"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void 과일_목록_조회_테스트() throws Exception {
        List<String> vegetables = Arrays.asList("배", "토마토", "사과", "바나나");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        ResponseEntity<List<String>> getResponseEntity = new ResponseEntity<>(vegetables, headers, HttpStatus.OK);

        AccessToken accessToken = new AccessToken("token");
        ResponseEntity<AccessToken> tokenResponseEntity = new ResponseEntity<>(accessToken, headers, HttpStatus.OK);

        given(restTemplate.exchange(
                eq(new URI("http://fruit.api.postype.net/token")),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<AccessToken>() {})))
                .willReturn(tokenResponseEntity);
        given(restTemplate.exchange(
                eq(new URI("http://fruit.api.postype.net/product")),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<List<String>>() {})))
                .willReturn(getResponseEntity);

        mockMvc.perform(get("/greens?type=FRUIT"))
                .andExpect(status().isOk());
    }

    @Test
    public void 과일_조회_테스트() throws Exception {
        Product product = new Product("사과", 7000);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        ResponseEntity<Product> getResponseEntity = new ResponseEntity<>(product, headers, HttpStatus.OK);

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
                        .addParameter("name", "사과")
                        .build()),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<Product>() {})))
                .willReturn(getResponseEntity);

        mockMvc.perform(get("/greens/FRUIT?name=사과"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("사과")))
                .andExpect(jsonPath("$.price", is(7000)));
    }

    @Test
    public void 과일_조회_실패_테스트() throws Exception {
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
                        .addParameter("name", "샤인머스켓")
                        .build()),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<Product>() {})))
                .willThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/greens/FRUIT?name=샤인머스켓"))
                .andExpect(status().is4xxClientError());
    }
}
