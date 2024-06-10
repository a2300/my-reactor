package com.example;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
public class OrderHandler {
    final OrderRepository orderRepository;

    public OrderHandler(OrderRepository repository) {
        orderRepository = repository;
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request
                .bodyToMono(Order.class)
                .flatMap(orderRepository::save)
                .flatMap(o ->
                        ServerResponse.created(URI.create("/orders/" + o.getId()))
                                .build()
                );
    }

    public Mono<ServerResponse> get(ServerRequest request) {
        return orderRepository
                .findById(request.pathVariable("id"))
                .flatMap(order ->
                        ServerResponse
                                .ok().bodyValue(order)
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> list(ServerRequest request) {
        return orderRepository
                .findAll()
                .collectList()
                .flatMap(orders -> {
                    if(orders.isEmpty()) {
                        return ServerResponse.notFound().build();
                    }
                    return ServerResponse.ok().bodyValue(orders);
                });
    }

    public Mono<ServerResponse> deleteById(ServerRequest request) {
        return orderRepository
                .deleteById(request.pathVariable("id"))
                .then(ServerResponse.ok().build());
    }
}
