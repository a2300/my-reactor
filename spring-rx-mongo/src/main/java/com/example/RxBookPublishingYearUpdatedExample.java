package com.example;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

import static java.time.Duration.between;
import static java.time.Instant.now;

@Slf4j
@Component
@RequiredArgsConstructor
public class RxBookPublishingYearUpdatedExample {
    private final BookSpringDataMongoRxRepository rxBookRepository;

    public void updatedBookYearByTitle() {
        Instant start = now();
        Mono<String> title = Mono
                .delay(Duration.ofSeconds(1))
                .thenReturn("Artemis")
                .doOnSubscribe(s -> log.info("Subscribed for title"))
                .doOnNext(t -> log.info("Book title resolved: {}" , t));

        Mono<Integer> publishingYear = Mono
                .delay(Duration.ofSeconds(2))
                .thenReturn(2017)
                .doOnSubscribe(s -> log.info("Subscribed for publishing year"))
                .doOnNext(t -> log.info("New publishing year resolved: {}" , t));

        updatedBookYearByTitle(title, publishingYear)
                .doOnNext(b -> log.info("Publishing year updated for the book: {}", b))
                .hasElement()
                .doOnSuccess(status -> log.info("Updated finished {}, took: {}",
                        status ? "successfully" : "unsuccessfully",
                        between(start, now())))
                .subscribe();
    }

    public Mono<Book> updatedBookYearByTitle(
            Mono<String> title,
            Mono<Integer> newPublishingYear
    ) {
        return rxBookRepository.findOneByTitle(title)
                .flatMap(book -> newPublishingYear
                        .flatMap(year -> {
                            book.setPublishingYear(year);
                            return rxBookRepository.save(book);
                        }));
    }
}
