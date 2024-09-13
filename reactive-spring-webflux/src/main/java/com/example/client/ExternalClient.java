package com.example.client;

import com.example.db.model.Payment;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

public interface ExternalClient {
    @PostExchange("/submit")
    Mono<String> submit(@RequestBody Payment payment);
}
