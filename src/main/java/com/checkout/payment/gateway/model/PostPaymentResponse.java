package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.enums.PaymentStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class PostPaymentResponse {
  private UUID id;
  private PaymentStatus status;
  private String cardNumberLastFour;
  private int expiryMonth;
  private int expiryYear;
  private String currency;
  private long amount;

  public PostPaymentResponse(PostPaymentRequest request, PaymentStatus status) {
    this.id = UUID.randomUUID();
    this.status = status;
    this.expiryMonth = request.getExpiryMonth();
    this.expiryYear = request.getExpiryYear();
    this.currency = request.getCurrency();
    this.amount = request.getAmount();

    String cardNumber = request.getCardNumber();
    this.cardNumberLastFour = cardNumber.substring(cardNumber.length() - 4);
  }
}
