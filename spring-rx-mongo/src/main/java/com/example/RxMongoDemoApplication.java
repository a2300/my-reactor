package com.example;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@EnableMongoRepositories
@SpringBootApplication
@RequiredArgsConstructor
public class RxMongoDemoApplication implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(RxMongoDemoApplication.class);

    private final BookSpringDataMongoRxRepository rxBookRepository;
    private final RxMongoTemplateQueryService rxMongoTemplateQueryService;

    private final RxBookPublishingYearUpdatedExample yearUpdatedExample;

    public static void main(String[] args) {
        SpringApplication.run(RxMongoDemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Flux<Book> books = Flux.just(
                new Book("The Martian", 2011, "Andy Weir"),
                new Book("Blue Mars", 1996, "Kim Stanley Robinson"),
                new Book("The War of the Worlds", 1898, "H. G. Wells"),
                new Book("Artemis", 2016, "Andy Weir"),
                new Book("The Expanse: Leviathan Wakes", 2011, "Daniel Abraham", "Ty Franck"),
                new Book("The Expanse: Caliban's War", 2012, "Daniel Abraham", "Ty Franck")
        );

        rxBookRepository
                .saveAll(books /*.toIterable()*/) // Iterator inserts all entities with one query
                .count()
                .doOnNext(amount -> log.info("{} books saved in DB", amount))
                .block();

        Flux<Book> allBooks = rxBookRepository.findAll();
        reportResults("All books in DB:", allBooks);

        Flux<Book> andyWeirBooks = rxBookRepository
                .findByAuthorsOrderByPublishingYearDesc(Mono.just("Andy Weir"));
        reportResults("All books by Andy Weir:", andyWeirBooks);

        reportResults("Search for books with title regexp:",
                rxBookRepository.findManyByTitleRegex("Exp.*"));

        Flux<Book> booksWithFewAuthors = rxBookRepository.booksWithFewAuthors();
        reportResults("Books with few authors:", booksWithFewAuthors);

        log.info("--- Custom Query Service with ReactiveMongoTemplate -----------------------------");
        reportResults("Search for books with Mars:",
                rxMongoTemplateQueryService.findBooksByTitle("Expanse"));

        log.info("--- Updating book's publishing year ---------------------------------------------");
        yearUpdatedExample.updatedBookYearByTitle();

    }

    private void reportResults(String message, Flux<Book> books) {
        books.map(Book::toString)
                .reduce(
                        new StringBuffer(),
                        (sb, b) -> sb.append(" - ")
                                .append(b)
                                .append("\n"))
                .doOnNext(sb -> log.info(message + "\n{}", sb))
                .subscribe();
    }
}
