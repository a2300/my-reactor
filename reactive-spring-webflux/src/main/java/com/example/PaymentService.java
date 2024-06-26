package com.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PaymentService {
    Flux<Payment> list();

    Mono<String> send(Mono<Payment> payment);
}
