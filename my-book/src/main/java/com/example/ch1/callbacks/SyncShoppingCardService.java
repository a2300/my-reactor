package com.example.ch1.callbacks;

import com.example.common.Input;
import com.example.common.Output;

import java.util.function.Consumer;

public class SyncShoppingCardService implements ShoppingCardService {
    @Override
    public void calculate(Input value, Consumer<Output> c) {
        // No blocking operation, better to immediately provide answer
        c.accept(new Output());
    }
}
