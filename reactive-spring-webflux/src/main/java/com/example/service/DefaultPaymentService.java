package com.example.service;

import com.example.client.ExternalClient;
import com.example.db.PaymentRepository;
import com.example.db.model.Payment;
import com.example.exception.RetryExhaustedException;
import com.example.exception.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.RetryBackoffSpec;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class DefaultPaymentService implements PaymentService {

    private static final String UPSTREAM_SERVICE_NAME = "bank-service";

    private final PaymentRepository paymentRepository;
    private final ExternalClient client;
    private final RetryBackoffSpec retryBackoffSpec;

    @Override
    public Flux<Payment> list() {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMapMany(paymentRepository::findAllByUser);
    }

    @Override
    public Mono<Payment> findOne(String id) {
        return paymentRepository.findById(id);
    }

    @Override
    public Mono<String> send(Mono<Payment> payment) {
        return payment.zipWith(
                        ReactiveSecurityContextHolder.getContext(),
                        (p, c) -> p.withUser(c.getAuthentication().getName())
                )
                .flatMap(p -> client.submit(p)
                        .retryWhen(retryBackoffSpec)
                        .onErrorMap(WebClientResponseException.class, ex ->
                                new ServiceUnavailableException(UPSTREAM_SERVICE_NAME, ex.getStatusCode().value()))
                        .onErrorMap(RetryExhaustedException.class, ex ->
                                new ServiceUnavailableException(UPSTREAM_SERVICE_NAME,
                                        HttpStatus.SERVICE_UNAVAILABLE.value()))
                        .then(paymentRepository.save(p)))
                .map(Payment::getId);
    }
}
