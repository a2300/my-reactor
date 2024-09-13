package com.example.myreactor;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * From <a href="https://www.baeldung.com/java-reactor-map-flatmap">Project Reactor: map() vs flatMap()</a>
 */
public class MapFlatMapTest {
    @Test
    void map_can_work() {
        Function<String, String> mapper = String::toUpperCase;

        Flux<String> inFlux = Flux.just("ibm", "com");
        Flux<String> outFlux = inFlux.map(mapper);

        StepVerifier.create(outFlux)
                .expectNext("IBM", "COM")
                .expectComplete()
                .verify();
    }

    @Test
    void flatMap_can_work() {
        Function<String, Publisher<String>> mapper = s -> Flux.just(s.toUpperCase().split(""));

        Flux<String> inFlux = Flux.just("ibm", "com");
        Flux<String> outFlux = inFlux.flatMap(mapper);

        List<String> output = new ArrayList<>();
        outFlux.subscribe(output::add);
        assertThat(output).containsExactlyInAnyOrder("I", "B", "M", "C", "O", "M");

    }
}
