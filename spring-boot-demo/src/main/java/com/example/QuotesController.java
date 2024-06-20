package com.example;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/quotes")
public class QuotesController {

    private QuotesService quotes;

    @Autowired
    public QuotesController(QuotesService quotes) {
        this.quotes = quotes;
    }


    @GetMapping()
    public List<String> getQuotes() {
        return quotes.getAllQuotes() ;
    }

    @GetMapping("/{index}")
    public String getQuoteByIndex(@PathVariable int index) {
          return quotes.getQuoteByIndex(index);
    }

    @GetMapping(path="/exception")
    public String getQuoteByIndexV2() {
        throw new QuoteIndexOutOfBoundsException();
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void addQuote(@RequestBody String quote) {
        quotes.addQuote(quote);
    }

}
