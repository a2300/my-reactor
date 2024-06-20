package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestRestTemplateTests {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void quotesEndpointShouldReturn3Quotes() {
        var results = this.testRestTemplate.getForObject("/api/quotes", List.class);
        assertEquals(3, results.size());
    }
}
