package com.microbank.document.feign;

import com.microbank.document.dto.response.TransactionDetailsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "transaction-service", url = "http://localhost:8085/api/v1/transaction")
public interface TransactionServiceClient {

    @GetMapping("/details/{transactionId}")
    TransactionDetailsResponse getTransactionDetailsByTransactionId(@PathVariable String transactionId);

}
