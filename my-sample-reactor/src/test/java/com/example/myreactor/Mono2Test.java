package com.example.myreactor;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Mono2Test {

    private static final Logger log = LoggerFactory.getLogger(Mono2Test.class);

    @Test
    void createMonoTest() {
        Mono<String> stream8 = Mono.fromCallable(() -> httpRequest());

        StepVerifier.create(stream8)
                .expectErrorMessage("IO error")
                .verify();
    }

    @Test
    void noDataMonoTest() {
        Mono<Void> noData = Mono.fromRunnable(() -> doLongAction());

        StepVerifier.create(noData)
                .expectSubscription()
                .expectNextCount(0)
                .expectComplete()
                .verify();
    }

    @Test
    public void shouldCreateDefer() {
        Mono<User> userMono = requestUserData(null);
        StepVerifier.create(userMono)
                .expectNextCount(0)
                .expectErrorMessage("Invalid user id")
                .verify();
    }

    private void doLongAction() {
        log.info("Long action");
    }

    private String httpRequest() {
        log.info("Making HTTP request");
        throw new RuntimeException("IO error");
    }

    public Mono<User> requestUserData(String userId) {
        return Mono.defer(() ->
                isValid(userId)
                        ? Mono.fromCallable(() -> requestUser(userId))
                        : Mono.error(new IllegalArgumentException("Invalid user id")));
    }

    private boolean isValid(String userId) {
        return userId != null;
    }

    private User requestUser(String id) {
        return new User();
    }

    static class User {
        public String id, name;
    }
}
