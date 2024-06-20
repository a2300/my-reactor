package com.example;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
public class LabCheckTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuotesService quotesServiceMock;

    @Test
    public void Step1_InitialQuoteEndpoint() throws Exception {
        when(quotesServiceMock.getAllQuotes()).thenReturn(new ArrayList<>(
                Arrays.asList("quote 1", "quote 2")));

        ResultActions resultAction = this.mockMvc.perform(get("")); //.andReturn();
        MvcResult result = resultAction.andReturn();

        if (result.getResponse().getStatus() == 404) {
            resultAction = this.mockMvc.perform(get("/api/quotes"));
        }

        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void Step2_GroupUnderAPIPath() throws Exception {
        when(quotesServiceMock.getAllQuotes()).thenReturn(new ArrayList<>(
                Arrays.asList("quote 1", "quote 2")));

        this.mockMvc.perform(get("/api/quotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

    }

    @Test
    public void Step3_PassAPathVariable() throws Exception {
        when(quotesServiceMock.getQuoteByIndex(0)).thenReturn("quote 1");

        this.mockMvc.perform(get("/api/quotes/0"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("quote 1")));

    }

    @Test
    public void Step4_PostNewQuote() throws Exception {
        when(quotesServiceMock.addQuote("quote")).thenReturn("quote");
        this.mockMvc.perform(post("/api/quotes").content("quote"))
                .andExpect(status().isCreated());
    }
}

