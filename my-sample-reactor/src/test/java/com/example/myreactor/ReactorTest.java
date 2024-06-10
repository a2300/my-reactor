package com.example.myreactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReactorTest {

    @Test
    void simpleFluxTest() {
        Flux<String> flux = Flux.just("red", "green", "blue");
        flux.subscribe(System.out::println);
    }

    @Test
    void simpleFluxTestWithLog() {
        Flux<String> flux = Flux.just("red", "green", "blue");
        flux.log().subscribe(System.out::println);
    }

    @Test
    void mapExample() {
        Flux<String> flux = Flux.just("red", "green", "blue");
        flux.map(color -> color.charAt(0)).subscribe(System.out::println);
    }

    @Test
    void zipExample() {
        Flux<String> fluxFruits = Flux.just("apple", "pear", "plum");
        Flux<String> fluxColors = Flux.just("red", "green", "blue");
        Flux<Integer> fluxAmounts = Flux.just(10, 20, 30);
        Flux.zip(fluxFruits, fluxColors, fluxAmounts).subscribe(System.out::println);
    }

    @Test
    void onErrorExample() {
        Flux<String> fluxCalc = Flux.just(-1, 0, 1).map(i -> "10 / " + i + " = " + (10 / i));
        fluxCalc.subscribe(value -> System.out.println("Next: " + value), error -> System.err.println(error));
    }

    @Test
    void onErrorReturnExample() {
        Flux<String> fluxCalc = Flux.just(-1, 0, 1).map(i -> "10 / " + i + " = " + (10 / i))
                .onErrorReturn(ArithmeticException.class, "Division by zero");

        fluxCalc.subscribe(value -> System.out.println("Next: " + value), error -> System.err.println(error));
    }

    @Test
    void onErrorReturnAndResumeWithMapExample() {
        Flux<String> fluxCalc = Flux.just(-1, 0, 1)
                .map(i -> {
                    try {
                        return "10 / " + i + " = " + (10 / i);
                    } catch (ArithmeticException e) {
                        return "NaN";
                    }
                });

        fluxCalc.subscribe(value -> System.out.println("Next: " + value), System.err::println);
    }

    @Test
    void onErrorReturnAndResumeWithFlatMapExample() {
        Flux<String> fluxCalc = Flux.just(-1, 0, 1)
                .flatMap(i -> {
                    try {
                        return Mono.just("10 / " + i + " = " + (10 / i));
                    } catch (ArithmeticException e) {
                        return Mono.just("NaN");
                    }
                });

        fluxCalc.subscribe(value -> System.out.println("Next: " + value), System.err::println);
    }

    @Test
    public void stepVerifierTest() {
        Flux<String> fluxCalc = Flux.just(-1, 0, 1)
                .map(i -> "10 / " + i + " = " + (10 / i));

        StepVerifier.create(fluxCalc)
                .expectNextCount(1)
                .expectError(ArithmeticException.class)
                .verify();
    }

    @Test
    void blockOptional() {
        Optional<String> result = Mono.<String>empty().blockOptional();
        assertEquals(Optional.empty(), result);
    }

    @Test
    void simpleMonoTest() {
        Mono<String> welcome = Mono.just("Welcome to bigdata").map(msg -> msg.concat(".com"));
        welcome.subscribe(System.out::println);

        StepVerifier.create(welcome)
                .expectNext("Welcome to bigdata.com")
                .verifyComplete();
    }

    @Test
    void emptyMonoTest() {
        Mono<Object> empty = Mono.empty().log();
        empty.subscribe(val -> {
            System.out.println(" === Value === " + val);
        }, err -> {

        }, () -> {
            System.out.println("====== On Complete Invoked ======");
        });

        //To Test
        StepVerifier.create(empty)
                .expectNextCount(0) //optional
                .verifyComplete();
    }

    @Test
    public void noSignalMono() {

        //Mono that never returns a value
        Mono<String> noSignal = Mono.never();

        //Can not use - Mono.never().log()
        noSignal.subscribe(val -> {
            System.out.println("==== Value ====" + val);
        });

        StepVerifier.create(noSignal)
                .expectTimeout(Duration.ofSeconds(5))
                .verify();
    }

    @Test
    public void subscribeMono3() {
        Mono<String> message$ = Mono.error(new RuntimeException("Check error mono"));

        message$.subscribe(
                value -> {
                    System.out.println(value);
                },
                err -> {
                    err.printStackTrace();
                },
                () -> {
                    System.out.println("===== Execution completed =====");
                });

        StepVerifier.create(message$)
                .expectError(RuntimeException.class)
                .verify();
    }


}
