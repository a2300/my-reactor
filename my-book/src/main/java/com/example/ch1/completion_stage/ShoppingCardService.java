package com.example.ch1.completion_stage;

import com.example.common.Input;
import com.example.common.Output;

import java.util.concurrent.CompletionStage;

public interface ShoppingCardService {
    CompletionStage<Output> calculate(Input value);
}
