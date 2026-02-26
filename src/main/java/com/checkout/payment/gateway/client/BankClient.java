package com.checkout.payment.gateway.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.checkout.payment.gateway.exception.BankServiceException;
import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.BankPaymentResponse;

@Component
public class BankClient {
    private static final String PAYMENT_API_PATH = "/payments";
    
    private final RestTemplate restTemplate;
    private final String bankUrl;

    public BankClient(RestTemplate restTemplate, @Value("${bank.simulator.url}") String bankUrl) {
        this.restTemplate = restTemplate;
        this.bankUrl = bankUrl;
    }

    public BankPaymentResponse processPayment(BankPaymentRequest request) {
        try {
            ResponseEntity<BankPaymentResponse> response = restTemplate.exchange(
                bankUrl + PAYMENT_API_PATH,
                HttpMethod.POST,
                new HttpEntity<>(request),
                BankPaymentResponse.class
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new BankServiceException("Unable to process payment", e);
        }
    }
}
