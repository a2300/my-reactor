package com.example.ch1.futures;

import com.example.common.Input;
import com.example.common.Output;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class FutureShoppingCardService implements ShoppingCardService {
    @Override
    public Future<Output> calculate(Input value) {
        FutureTask<Output> future = new FutureTask<>(() -> {
            Thread.sleep(1000);
            return new Output();
        });

        new Thread(future).start();

        return future;
    }
}
