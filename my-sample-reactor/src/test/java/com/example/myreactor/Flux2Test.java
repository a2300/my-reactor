package com.example.myreactor;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import static java.time.Duration.ofMillis;

public class Flux2Test {

    private static final Logger log = LoggerFactory.getLogger(Flux2Test.class);

    private final Random random = new Random();

    @Test
    void createFluxTest() {
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .log()
                .take(5)
                .skip(3)
                .subscribe(value -> System.out.println("Value " + value));
    }

    @Test
    void manualSubscriberTest() {
        List<Integer> elements = new ArrayList<>();

        Flux.just(1, 2, 3, 4)
                .log()
                .subscribe(new Subscriber<Integer>() {
                    private Subscription s;
                    int onNextAmount;

                    @Override
                    public void onSubscribe(Subscription s) {
                        this.s = s;
                        s.request(2);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        elements.add(integer);
                        onNextAmount++;
                        if (onNextAmount % 2 == 0) {
                            s.request(2);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {}

                    @Override
                    public void onComplete() {}
                });

        System.out.println(Arrays.toString(elements.toArray()));
    }

    @Test
    void manualSubscriber2Test() {
        List<Integer> elements = new ArrayList<>();
        Flux.just(1, 2, 3, 4)
                .log()
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        elements.add(integer);
                    }

                    @Override
                    public void onError(Throwable t) {}

                    @Override
                    public void onComplete() {}
                });
    }

    @Test
    void blockingOperationsTest() {
        Mono<List<Integer>> listMono = Flux.just(1, 2, 3, 4)
                .filter(value -> value % 2 == 0)
                .collectList();
        System.out.println(listMono.block());
    }

    @Test
    void mapTest() {
        Flux.just(1, 2, 3)
                .map(s -> s + 1)
                .subscribe(System.out::println);
    }

    @Test
    void flatMapTest() {
        Flux.just("1,2,3", "4,5,6")
                .flatMap(i -> Flux.fromIterable(Arrays.asList(i.split(","))))
                .collectList()
                .subscribe(System.out::println);
    }

    @Test
    void flatMap3Test() {
        List<String> list = Arrays.asList("a", "b", "c", "d", "e", "f");

        Flux.fromIterable(list)
                .flatMap(x -> Flux.just(x + "s"))
                .collectList()
                .subscribe(System.out::println);
    }

    @Test
    void flatMap4Test() {
        Flux.range(1, 10)
                .flatMap(e -> {
                    if (e < 5) {
                        return Flux.just(e * e);
                    }
                    return Flux.error(new IOException("Error: "));
                }).subscribe(System.out::println, Throwable::printStackTrace);
    }

    @Test
    void flatMap5Test() {
        Flux.range(1, 3)
                .flatMap(e -> Flux.just(e + "x"))
                .subscribe(System.out::println);
    }

    @Test
    void flatMap6Test() {
        Flux<Integer> numbers = Flux.just(1, 2, 3);
        Flux<String> letters = numbers
                .flatMap(number -> Flux.just("A", "B").map(letter -> number + letter));

        letters.subscribe(System.out::println, Throwable::printStackTrace);
    }

    @Test
    void errorTest() {
        Flux<Integer> numbers = Flux.just(1, 2, 3, 4, 5)
                .concatWith(Flux.error(new RuntimeException("Oops! An error occurred.")))
                .map(number -> 10 / (number - 3)) // This will cause an ArithmeticException

                .doOnError(throwable -> System.err.println("Error occurred: " + throwable.getMessage()))

                .onErrorReturn(-1); // Provide a fallback value in case of an error

        numbers.subscribe(
                value -> System.out.println("Received: " + value),
                error -> System.err.println("Subscriber error: " + error.getMessage())
        );
    }

    @Test
    void testWithError() {
        Flux<Integer> numbers = Flux.just(1, 2, 3)
                .concatWith(Flux.error(new RuntimeException("Oops! An error occurred.")));

        StepVerifier.create(numbers)
                .expectNext(1, 2, 3)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void mergeTest() {
        Flux<Long> interval1 = Flux.interval(Duration.ofSeconds(1)).take(3);
        Flux<Long> interval2 = Flux.interval(Duration.ofMillis(500), Duration.ofMillis(1000)).take(3);

        Flux<Long> merged = Flux.merge(interval1, interval2);

        merged.subscribe(System.out::println);  // Output: 0 0 1 1 2 2
    }



    @Test
    void mapsGeneralTest() throws InterruptedException {
        System.out.println("Using flatMap():");
        Flux.range(1, 15)
                .flatMap(item -> Flux.just(item).delayElements(ofMillis(1)))
                .subscribe(x -> System.out.print(x + " "));

        Thread.sleep(100);

        System.out.println("\n\nUsing concatMap():");
        Flux.range(1, 15)
                .concatMap(item -> Flux.just(item).delayElements(ofMillis(1)))
                .subscribe(x -> System.out.print(x + " "));

        Thread.sleep(300);
        System.out.println("\n\nUsing switchMap():");
        Flux.range(1, 15)
                .switchMap(item -> Flux.just(item).delayElements(ofMillis(1)))
                .subscribe(x -> System.out.print(x + " "));

        Thread.sleep(300);
    }

    @Test
    void runOnTest() {
        Flux.range(1, 10)
                .parallel(2)
                .runOn(Schedulers.parallel())
                .subscribe(i -> System.out.println(Thread.currentThread().getName() + " -> " + i));
    }

    @Test
    void rangeTest() {
        Flux.range(1, 3)
                .startWith(Mono.just(90))
                .subscribe(System.out::println);
    }

    @Test
    void zipTest() {
        Flux.just("a", "b", "c")
                .zipWith(Flux.just(1, 2, 3), (word, number) -> word + number)
                .subscribe(System.out::println);
    }

    @Test
    void concatTest() {
        Flux<Integer> oddFlux = Flux.just(1, 3);
        Flux<Integer> evenFlux = Flux.just(2, 4);

        Flux.concat(evenFlux, oddFlux)
                .subscribe(value -> System.out.println("Outer: " + value));
    }

    @Test
    void groupByTest() {
        Flux.just(1, 3, 5, 2, 4, 6, 11, 12, 13)
                .groupBy(i -> i % 2 == 0 ? "even:" : "odd:")
                .concatMap(Flux::collectList)
                .subscribe(System.out::println);
    }

    private int testNumbers(int value) {
        if (value > 4) {
            System.out.println("Test checked");
        }
        return value;
    }

    @Test
    void testRepeat() {
        Flux.range(1, 5)
                .map(this::testNumbers)
                .repeat(1)
                .subscribe(value -> System.out.println("Value: " + value),
                        error -> System.out.println("Error: " + error.getMessage()));
    }

    @Test
    void testRepeatWithError() {
        Flux.range(1, 5)
                .map(num -> {
                    if(num > 2) {
                        System.out.println("Test checked");
                        throw new IllegalArgumentException("value is too high!");
                    }
                    return num;
                })
                .repeat(1)
                .subscribe(value -> System.out.println("Value: " + value),
                        error -> System.out.println("Error: " + error.getMessage()));
    }

    @Test
    void retryTest() {
        Flux.just(1, 2, 3)
                .map(number -> {
                    if(number > 2) {
                        throw new IllegalArgumentException("value is too high!");
                    }
                    return number;
                })
                .retry(1)
                .subscribe(value -> System.out.println("Value: " + value),
                        error -> System.out.println("Error: " + error.getMessage()));
    }

    @Test
    void blockLastTest() {
        Flux.range(1, 3)
                .delayElements(Duration.ofMillis(1000))
                .doOnNext(System.out::println)
                .blockLast();
    }

    @Test
    void doNextTest() {
        Flux.just("one", "two", "three")
                .doOnNext(item -> {
                    if (item.equals("one")) System.out.println("First item: " + item);
                    else if (item.equals("three")) System.out.println("Last item: " + item);
                })
                .subscribe();
    }

    @Test
    void doFinallyTest() {
        Flux.just(1, 2, 3)
                .map(number -> {
                    if(number > 2) {
                        throw new IllegalArgumentException("value is too high!");
                    }
                    return number;
                })
                .doFinally(signalType -> {
                    if (signalType == SignalType.ON_ERROR) {
                        System.out.println("Error signal");
                    } else if (signalType == SignalType.CANCEL) {
                        System.out.println("Cancel signal");
                    }
                })
                .subscribe(value -> System.out.println("Value: " + value),
                        error -> System.out.println("Error: " +
                                error.getMessage()));
    }

    @Test
    void blockTest() {
        String result2 = Mono.just("one")
                .map(String::toUpperCase)
                .block();
        System.out.println(result2);
    }

    @Test
    void blockLast2Test() {
        Flux<Integer> sequence = Flux.range(0,10)
                .publishOn(Schedulers.single());   // onNext, onComplete и onError будут происходить в "single" sheduler.

        sequence.subscribe(n -> {
            System.out.println("n = " + n);
            System.out.println("Thread.currentThread() = " + Thread.currentThread());
        });

        sequence.blockLast();
    }

    @Test
    void onErrorReturnTest() {
        Flux.just(1, 2, 3)
                .map(number -> {
                    if(number > 2) {
                        throw new IllegalArgumentException("value is too high!");
                    }
                    return number;
                })
                .onErrorReturn(100)
                .subscribe(value -> System.out.println("Value: " + value));
    }

    @Test
    void onErrorResumeTest() {
        Flux.just(1, 2, 3)
                .map(number -> {
                    if(number > 2) {
                        throw new IllegalArgumentException("value is too high!");
                    }
                    return number;
                })
                .onErrorResume(error -> Flux.just(4, 5, 6))
                .subscribe(value -> System.out.println("Value: " + value));
    }

    private Stream<String> getMovie(){
        System.out.println("Start streaming...");
        return Stream.of(
                "thread 1",
                "thread 2",
                "thread 3",
                "thread 4",
                "thread 5"
        );
    }

    @Test
    void coldPublisherTest() throws InterruptedException {
        Flux<String> streamingVideo = Flux.fromStream(() -> getMovie())
                .delayElements(Duration.ofSeconds(2));

        // Первый человек начал смотреть фильм "Titanic"
        streamingVideo.subscribe(scene -> System.out.println("First person is watching " + scene));

        // Второй человек начал смотреть фильм "Titanic" спустя 5 секунд
        Thread.sleep(5000);
        streamingVideo.subscribe(scene -> System.out.println("Second person is watching " + scene));

        Thread.sleep(5000);
    }

    @Test
    void subscriberTest() {
        Subscriber<String> subscriber = new MySubscriber<String>();
        Flux<String> stream = Flux.just("Hello", "world", "!");
        stream.subscribe(subscriber);
    }

    @Test
    void anyTest() {
        Flux.just(3, 5, 7, 9, 11, 15, 16, 17)
                .any(e -> e % 2 == 0)
                .subscribe(hasEvens -> System.out.println("Has evens: " + hasEvens));
    }

    @Test
    void bufferTest() {
        Flux.range(1, 13)
                .buffer(4)
                .subscribe(e -> log.info("onNext: {}", e));
    }


    @Test
    public void managingSubscription() throws InterruptedException {
        Disposable disposable = Flux.interval(Duration.ofMillis(50))
                .doOnCancel(() -> log.info("Cancelled"))
                .subscribe(
                        data -> log.info("onNext: {}", data)
                );
        Thread.sleep(200);
        disposable.dispose();
    }

    @Test
    public void thenOperator() {
        Flux.just(1, 2, 3)
                .thenMany(Flux.just(5, 6))
                .subscribe(e -> log.info("onNext: {}", e));
    }

    private Flux<String> requestBooks(String user) {
        return Flux.range(1, random.nextInt(3) + 1)
                .delayElements(Duration.ofMillis(3))
                .map(i -> "book-" + i);
    }

    @Test
    public void flatMapExample() throws InterruptedException {
        Flux.just("user-1", "user-2", "user-3")
                .flatMap(u -> requestBooks(u)
                        .map(b -> u + "/" + b))
                .subscribe(r -> log.info("onNext: {}", r));

        Thread.sleep(1000);
    }

    @Test
    public void tryWithResources() {
        try (Connection conn = Connection.newConnection()) {
            conn.getData().forEach(
                    data -> log.info("Received data: {}", data)
            );
        } catch (Exception e) {
            log.info("Error: {}", e.getMessage());
        }
    }

    @Test
    public void usingOperator() {
        Flux<String> ioRequestResults = Flux.using(
                Connection::newConnection,
                connection -> Flux.fromIterable(connection.getData()),
                Connection::close
        );

        ioRequestResults
                .subscribe(
                        data -> log.info("Received data: {}", data),
                        e -> log.info("Error: {}", e.getMessage()),
                        () -> log.info("Stream finished"));
    }

    static class Connection implements AutoCloseable {
        private final Random rnd = new Random();

        static Connection newConnection() {
            log.info("IO Connection created");
            return new Connection();
        }

        public Iterable<String> getData() {
            if (rnd.nextInt(10) < 3) {
                throw new RuntimeException("Communication error");
            }
            return Arrays.asList("Some", "data");
        }

        @Override
        public void close() {
            log.info("IO Connection closed");
        }
    }

    static class Transaction {
        private static final Random random = new Random();
        private final int id;

        public Transaction(int id) {
            this.id = id;
            log.info("[T: {}] created", id);
        }

        public static Mono<Transaction> beginTransaction() {
            return Mono.defer(() ->
                    Mono.just(new Transaction(random.nextInt(1000))));
        }

        public Flux<String> insertRows(Publisher<String> rows) {
            return Flux.from(rows)
                    .delayElements(Duration.ofMillis(100))
                    .flatMap(row -> {
                        if (random.nextInt(10) < 2) {
                            return Mono.error(new RuntimeException("Error on: " + row));
                        } else {
                            return Mono.just(row);
                        }
                    });
        }


        public Mono<Void> commit() {
            return Mono.defer(() -> {
                log.info("[T: {}] commit", id);
                if (random.nextBoolean()) {
                    return Mono.empty();
                } else {
                    return Mono.error(new RuntimeException("Conflict"));
                }
            });
        }

        public Mono<Void> rollback() {
            return Mono.defer(() -> {
                log.info("[T: {}] rollback", id);
                if (random.nextBoolean()) {
                    return Mono.empty();
                } else {
                    return Mono.error(new RuntimeException("Conn error"));
                }
            });
        }
    }

    @Test
    public void usingWhenExample() throws InterruptedException {
        Flux.usingWhen(
                Transaction.beginTransaction(),
                transaction -> transaction.insertRows(Flux.just("A", "B")),
                Transaction::commit
        ).subscribe(
                d -> log.info("onNext: {}", d),
                e -> log.info("onError: {}", e.getMessage()),
                () -> log.info("onComplete")
        );

        Thread.sleep(1000);
    }

    public Flux<String> recommendedBooks(String userId) {
        return Flux.defer(() -> {
            if (random.nextInt(10) < 7) {
                return Flux.<String>error(new RuntimeException("Conn error"))
                        .delaySequence(Duration.ofMillis(100));
            } else {
                return Flux.just("Blue Mars", "The Expanse")
                        .delayElements(Duration.ofMillis(50));
            }
        }).doOnSubscribe(s -> log.info("Request for {}", userId));
    }

    @Test
    public void handlingErrors() throws InterruptedException {
        Flux.just("user-1")
                .flatMap(user ->
                        recommendedBooks(user)
                                .retryWhen(Retry.backoff(5, Duration.ofMillis(100)))
                                .timeout(Duration.ofSeconds(3))
                                .onErrorResume(e -> Flux.just("The Martian"))
                )
                .subscribe(
                        b -> log.info("onNext: {}", b),
                        e -> log.warn("onError: {}", e.getMessage()),
                        () -> log.info("onComplete")
                );

        Thread.sleep(5000);
    }

    @Test
    public void coldPublisher() {
        Flux<String> coldPublisher = Flux.defer(() -> {
            log.info("Generating new items");
            return Flux.just(UUID.randomUUID().toString());
        });

        log.info("No data was generated so far");
        coldPublisher.subscribe(e -> log.info("onNext: {}", e));
        coldPublisher.subscribe(e -> log.info("onNext: {}", e));
        log.info("Data was generated twice for two subscribers");
    }

}
