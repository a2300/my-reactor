package com.example;

import com.myapp.ApiClient;
import com.myapp.api.PetApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class Config {
    @Bean
    ApiClient apiClient(RestClient.Builder builder) {
        var apiClient = new ApiClient(builder.build());
        apiClient.setUsername("admin");
        apiClient.setPassword("admin");
        apiClient.setBasePath("http://localhost:9966/petclinic/api");
        return apiClient;
    }

    @Bean
    PetApi petApi(ApiClient apiClient) {
        return new PetApi(apiClient);
    }
}
