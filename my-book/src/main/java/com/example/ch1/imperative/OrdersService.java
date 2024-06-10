package com.example.ch1.imperative;

import com.example.common.Input;
import com.example.common.Output;

public class OrdersService {
    private final ShoppingCardService scService;

    public OrdersService(ShoppingCardService scService) {
        this.scService = scService;
    }

    public void process() {
        Input input = new Input();
        Output output = scService.calculate(input);

        System.out.println(scService.getClass().getSimpleName() + " execution completed");
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        new OrdersService(new BlockingShoppingCardService()).process();
        new OrdersService(new BlockingShoppingCardService()).process();

        System.out.println("Total elapsed time in millis is : " + (System.currentTimeMillis() - start));


    }
}
