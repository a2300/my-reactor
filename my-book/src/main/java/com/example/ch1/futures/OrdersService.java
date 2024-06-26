package com.example.ch1.futures;

import com.example.common.Input;
import com.example.common.Output;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class OrdersService {
    private final ShoppingCardService shoppingCardService;

    public OrdersService(ShoppingCardService shoppingCardService) {
        this.shoppingCardService = shoppingCardService;
    }

    void process() {
        Input input = new Input();
        Future<Output> result = shoppingCardService.calculate(input);

        System.out.println(shoppingCardService.getClass().getSimpleName() + " execution completed");

        try {
            result.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        OrdersService ordersService1 = new OrdersService(new FutureShoppingCardService());

        ordersService1.process();
        ordersService1.process();

        System.out.println("Total elapsed time in millis is : " + (System.currentTimeMillis() - start));
    }
}
