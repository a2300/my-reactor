package com.example;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
public class OrderController {

    private final ReactiveOrderService orderService;

    @GetMapping("/orders")
    public Flux<Order> getAllUsers(@RequestParam(required = false) String name) {
        if(name != null) {
            return orderService.getAllOrdersByName(name);
        }
        return orderService.getAllOrders();
    }

    @GetMapping("/orders/{id}")
    public Mono<ResponseEntity<Order>> getUserById(@PathVariable String id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/orders")
    public Mono<ResponseEntity<Order>> createUser(@RequestBody Order order) {
        return orderService.createOrder(order)
                .map(createdOrder -> ResponseEntity.status(HttpStatus.CREATED).body(createdOrder));
    }

    @DeleteMapping("/orders/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return orderService.deleteById(id);
    }

    @PutMapping("/orders/{id}")
    public Mono<Order> update(@PathVariable("id") String id, @RequestBody Order order) {
        return orderService.updateOrder(id, order);
    }

    @GetMapping("/search")
    public Flux<Order> search(
            @RequestParam() String q,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        return this.orderService.findByTitleLike(q, page, size);
    }

}
