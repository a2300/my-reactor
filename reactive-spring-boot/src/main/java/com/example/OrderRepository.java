package com.example;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import org.springframework.data.domain.Pageable;


public interface OrderRepository extends ReactiveMongoRepository<Order, String> {
    Flux<Order> findAllByName(String name);

    Flux<Order> findByNameContains(String name);

    Flux<Order> findByNameLike(String name, Pageable page);
}
