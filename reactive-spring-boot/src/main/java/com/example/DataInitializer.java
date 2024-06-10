package com.example;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Component
@Slf4j
@Profile("default")
public class DataInitializer implements CommandLineRunner {

    private final OrderRepository orderRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("start data initialization ...");
        orderRepository
                .deleteAll()
                .thenMany(Flux.just("book", "cake")
                        .flatMap(name -> orderRepository.save(Order.builder().name(name).quantity(10).build()))
                ).thenMany(orderRepository.findAll())
                .subscribe(
                        data -> log.info("founded orders: {}", data),
                        error -> log.error("" + error),
                        () -> log.info("initialization done...")
                );
    }
}
