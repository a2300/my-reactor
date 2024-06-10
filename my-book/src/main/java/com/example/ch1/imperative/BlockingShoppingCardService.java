package com.example.ch1.imperative;

import com.example.common.Input;
import com.example.common.Output;

public class BlockingShoppingCardService implements ShoppingCardService {
    @Override
    public Output calculate(Input value) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new Output();
    }
}
