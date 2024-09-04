package a2300.spring.client;

import a2300.spring.client.model.ExchangeRate;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface ExchangeClient {
    @GetExchange("/exchanges")
    ExchangeRate getExchangeRate(@RequestParam("currency") String currency);
}
