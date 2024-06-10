package com.example.ch2;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelSubject implements Subject<String> {
    private final Set<Observer<String>> observers =
            new CopyOnWriteArraySet<>();

    public void registerObserver(Observer<String> observer) {
        observers.add(observer);
    }

    public void unregisterObserver(Observer<String> observer) {
        observers.remove(observer);
    }

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public void notifyObservers(String event) {
        observers.forEach(observer ->
                executorService.submit(
                        () -> observer.observe(event)
                )
        );
    }
}
