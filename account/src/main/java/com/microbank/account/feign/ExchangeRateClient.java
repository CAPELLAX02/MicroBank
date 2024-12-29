package com.microbank.account.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@FeignClient(name = "exchange-rate-service", url = "http://localhost:80")
public interface ExchangeRateClient {

    @GetMapping("/latest/currencies.json")
    Map<String, Double> getExchangeRates();

}
