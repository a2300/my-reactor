package com.example.myreactor;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Flux3Test {

    private static final Logger log = LoggerFactory.getLogger(Flux3Test.class);

    @Test
    void simpleVerifier() {
        StepVerifier
                .create(Flux.just("foo", "boo"))
                .expectSubscription()
                .expectNext("foo")
                .expectNext("boo")
                .expectComplete()
                .verify();
    }

    @Test
    void predicateVerifier() {
        StepVerifier
                .create(Flux.just("alpha-foo", "betta-bar"))
                .expectSubscription()
                .expectNextMatches(e -> e.startsWith("alpha"))
                .expectNextMatches(e -> e.startsWith("betta"))
                .expectComplete()
                .verify();
    }

    @Test
    void verifyGeneratedValue() {
        StepVerifier
                .create(Flux.range(0, 100))
                .expectSubscription()
                .expectNext(0)
                .expectNextCount(98)
                .expectNext(99)
                .expectComplete()
                .verify();
    }

    @Test
    void verifyGeneratedPerson() {
        List<Person> list = new ArrayList<>();
        list.add(new Person("John", "john@gmail.com", "12345678"));
        list.add(new Person("Jack", "jack@gmail.com", "12345678"));

        Flux<Person> persons = Flux.fromIterable(list);

        StepVerifier
                .create(persons)
                .expectSubscription()
                .recordWith(ArrayList::new)
                .expectNextCount(2)
                .consumeRecordedWith(ps ->
                        assertThat(ps, everyItem(hasProperty("phone", equalTo("12345678")))))
                .expectComplete()
                .verify();
    }

    @Test
    void verifyErrorGenerated() {
        StepVerifier
                .create(Flux.error(new RuntimeException("Error")))
                .expectError()
                .verify();
    }

    @Test
    void verifyNoData() {
        Mono<Void> noData = Mono.fromRunnable(() -> doLongAction());

        StepVerifier.create(noData)
                .expectSubscription()
                .expectNextCount(0)
                .expectComplete()
                .verify();        
    }

    private void doLongAction() {
        log.info("Long action");
    }

    @Test
    public void shouldCreateDefer() {
        Mono<Person> personMono = requestPersonData(null);
        StepVerifier.create(personMono)
                .expectNextCount(0)
                .expectErrorMessage("Invalid user id")
                .verify();
    }

    private Mono<Person> requestPersonData(String personId) {
        return Mono.defer(() ->
                isValid(personId)
                        ? Mono.fromCallable(() -> requestPerson(personId))
                        : Mono.error(new IllegalArgumentException("Invalid user id")));
    }

    private boolean isValid(String personId) {
        return personId != null;
    }

    private Person requestPerson(String id) {
        return new Person("Boo", "boo@mail.com", "11223344");
    }


    @Test
    void sendWithIntervalTest()  {
        StepVerifier
                .withVirtualTime(() -> sendWithInterval())
                .expectSubscription()
                .thenAwait(Duration.ofMinutes(3))
                .expectNext("a", "b", "c")
                .expectComplete()
                .verify();
    }


    private Flux<String> sendWithInterval() {
        return Flux.interval(Duration.ofMinutes(1))
                .zipWith(Flux.just("a", "b", "c"))
                .map(Tuple2::getT2);
    }


}
