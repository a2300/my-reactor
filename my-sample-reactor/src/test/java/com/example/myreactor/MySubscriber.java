package com.example.myreactor;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;

public class MySubscriber<T> extends BaseSubscriber<T> {
    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        System.out.println("initial request for 1 element");
        request(1);
    }

    @Override
    protected void hookOnNext(T value) {
        System.out.println("onNext: " +  value);
        System.out.println("requesting 1 more element");
        request(1);
    }
}
