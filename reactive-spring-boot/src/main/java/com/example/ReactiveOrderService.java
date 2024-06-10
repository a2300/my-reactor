package com.example;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ReactiveOrderService {

    private final OrderRepository repository;
    public Flux<Order> getAllOrders() {
        return repository.findAll();
    }

    public Mono<Order> getOrderById(String id) {
        return repository.findById(id);
    }

    public Mono<Order> createOrder(Order order) {
        return repository.insert(order);
    }

    public Flux<Order> getAllOrdersByName(String name) {
        return repository.findAllByName(name);
    }

    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }

    public Mono <Order> updateOrder(String id, Order order) {
        return this.repository.findById(id)
                .map(p -> {
                    p.setName(order.getName());
                    p.setQuantity(order.getQuantity());

                    return p;
                })
                .flatMap(repository::save);
    }

    public Flux<Order> findByTitleLike(String q, Integer page, Integer size) {
        return repository.findByNameLike(q, PageRequest.of(page, size));
    }
}
