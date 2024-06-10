package com.example.ch1.callbacks;

import com.example.common.Input;
import com.example.common.Output;

import java.util.function.Consumer;

public interface ShoppingCardService {
    void calculate(Input value, Consumer<Output> c);
}
