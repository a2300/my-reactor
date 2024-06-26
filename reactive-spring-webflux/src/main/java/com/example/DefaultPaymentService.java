package com.example;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.security.Principal;

@Service
public class DefaultPaymentService implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final WebClient client;

    public DefaultPaymentService(PaymentRepository repository,
                                 WebClient.Builder builder) {
        paymentRepository = repository;
        client = builder.baseUrl("http://api.bank.com/submit").build();
    }

    @Override
    public Flux<Payment> list() {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMapMany(paymentRepository::findAllByUser);
    }

    @Override
    public Mono<String> send(Mono<Payment> payment) {
        return payment.zipWith(
                        ReactiveSecurityContextHolder.getContext(),
                        (p, c) -> p.withUser(c.getAuthentication().getName())
                )
                .flatMap(p -> client.post()
                        .bodyValue(p)
                        .retrieve()
                        .bodyToMono(String.class)
                        .then(paymentRepository.save(p)))
                .map(Payment::getId);
    }
}
