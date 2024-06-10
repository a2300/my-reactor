package com.example.ch1.futures;

import com.example.common.Input;
import com.example.common.Output;

import java.util.concurrent.Future;

public interface ShoppingCardService {
    Future<Output> calculate(Input value);
}
