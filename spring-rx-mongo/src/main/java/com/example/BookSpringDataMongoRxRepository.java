package com.example;

import org.bson.types.ObjectId;
import org.reactivestreams.Publisher;
import org.springframework.data.mongodb.repository.Meta;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BookSpringDataMongoRxRepository extends ReactiveMongoRepository<Book, ObjectId> {

    Mono<Book> findOneByTitle(Mono<String> title);

    Flux<Book> findManyByTitleRegex(String regexp);

    @Query("{ 'authors.1': { $exists: true } }")
    Flux<Book> booksWithFewAuthors();
    @Meta(cursorBatchSize = 3)
    Flux<Book> findByAuthorsOrderByPublishingYearDesc(Publisher<String> authors);
}
