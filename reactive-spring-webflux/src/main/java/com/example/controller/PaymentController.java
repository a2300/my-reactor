package com.example.controller;

import com.example.dto.PaymentDto;
import com.example.mapper.PaymentMapper;
import com.example.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentMapper mapper;


    @GetMapping("")
    public Flux<PaymentDto> list() {
        return paymentService.list().map(mapper::mapToDto);
    }

    @GetMapping("/{id}")
    public Mono<PaymentDto> findOne(@PathVariable String id) {
        return paymentService.findOne(id).map(mapper::mapToDto);
    }

    @PostMapping("")
    public Mono<String> send(Mono<PaymentDto> payment) {
        return  payment.map(mapper::mapToDb)
                .flatMap(p -> paymentService.send(Mono.just(p)));
    }
}
