package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public RouterFunction<ServerResponse> routes(OrderHandler orderHandler) {
        return
                nest(path("/orders"),
                        nest(accept(APPLICATION_JSON),
                                route(GET("/{id}"), orderHandler::get)
                                        .andRoute(method(HttpMethod.GET), orderHandler::list)
                                        .andRoute(DELETE("/{id}"), orderHandler::deleteById)
                        )
                                .andNest(contentType(APPLICATION_JSON),
                                        route(POST("/"), orderHandler::create)
                                )

                );
    }
}
