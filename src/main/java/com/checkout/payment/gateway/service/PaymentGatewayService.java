package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.client.BankClient;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.BankPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;

import java.util.UUID;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final BankClient bankClient;

  private final PaymentsRepository paymentsRepository;

  public PaymentGatewayService(BankClient bankClient, 
                               PaymentsRepository paymentsRepository) {
    this.bankClient = bankClient;
    this.paymentsRepository = paymentsRepository;
  }

  public PostPaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to to payment with ID {}", id);
    return paymentsRepository.get(id).orElseThrow(() -> new EventProcessingException("Invalid ID"));
  }

  public PostPaymentResponse processPayment(PostPaymentRequest request) {
    BankPaymentRequest bankPaymentRequest = 
        BankPaymentRequest.builder()
          .cardNumber(request.getCardNumber())
          .expiryDate(request.getExpiryString())
          .currency(request.getCurrency())
          .amount(request.getAmount())
          .cvv(request.getCvv())
          .build();

    BankPaymentResponse bankPaymentResponse = bankClient.processPayment(bankPaymentRequest);

    PaymentStatus status = bankPaymentResponse.isAuthorized() ? PaymentStatus.AUTHORIZED : PaymentStatus.DECLINED;
    PostPaymentResponse payment = new PostPaymentResponse(request, status);
    paymentsRepository.add(payment);

    return payment;
  }
}
