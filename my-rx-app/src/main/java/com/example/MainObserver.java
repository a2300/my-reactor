package com.example;

import rx.Observable;

public class MainObserver {
    public static void main(String[] args) throws InterruptedException {
        Observable.create(
                sub -> {
                    sub.onNext("Hello, reactive world!");
                    sub.onCompleted();
                }
        ).subscribe(
                System.out::println,
                System.err::println,
                () -> System.out.println("Done!")
        );

        Observable.just("a", "b", "c", "d")
                .map(String::toUpperCase)
                .subscribe(
                        System.out::println,
                        System.err::println,
                        () -> System.out.println("Done!")
                );

        Observable.just(1, 2, 3, 4)
                .filter(i -> i % 2 == 0)
                .count()
                .subscribe(System.out::println);

        Observable.zip(
                Observable.just("A", "B", "C"),
                Observable.just("1", "2", "3"),
                (x, y) -> x + y
        ).forEach(System.out::println);

        slowMethod()
                .doOnNext(System.out::println)
                .map(String::toUpperCase)
                .subscribe(System.out::println);


        Thread.sleep(1000);
    }

    static Observable<String> slowMethod() throws InterruptedException {
        Thread.sleep(100);
        return Observable.defer(() -> Observable.just("Hello from slow"));
    }
}
