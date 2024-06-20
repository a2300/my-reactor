package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BasicSetupTests {
    @Autowired
    private QuotesService quotesService;

    @Test
    public void quotesServiceShouldReturn3Quotes() {
        var quotes = quotesService.getAllQuotes();
        assertEquals(3, quotes.size());
    }
}
