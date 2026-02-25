package com.checkout.payment.gateway.validation;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.checkout.payment.gateway.model.PostPaymentRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.assertj.core.api.Assertions.assertThat;

public class PostPaymentRequestTest {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testRequest_validRequest() {
        PostPaymentRequest request = PostPaymentRequest.builder()
                .cardNumber("12345678901234")
                .expiryMonth(12)
                .expiryYear(2027)
                .currency("USD")
                .amount(1000L)
                .cvv("123")
                .build();
    
        Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(request);
    
        assertTrue(violations.isEmpty());
    }

    // TODO: Make assertions check more details

    @Test
    public void testRequest_missingFields() {
        PostPaymentRequest request = PostPaymentRequest.builder().build();
    
        Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(request);
    
        Set<String> paths = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());
        assertThat(paths).contains("cardNumber", "currency", "amount", "expiryMonth", "expiryYear", "cvv");
    }

    @Test
    public void testRequest_cardNumberTooShort() {
        PostPaymentRequest request = PostPaymentRequest.builder()
                .cardNumber("123")
                .expiryMonth(12)
                .expiryYear(2099)
                .currency("USD")
                .amount(1000L)
                .cvv("123")
                .build();
    
        Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(request);
    
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testRequest_cardNumberContainsInvalidCharacter() {
        PostPaymentRequest request = PostPaymentRequest.builder()
                .cardNumber("1234567890abcd")
                .expiryMonth(12)
                .expiryYear(2027)
                .currency("USD")
                .amount(1000L)
                .cvv("123")
                .build();
    
        Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(request);
    
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testRequest_invalidExpiryMonth() {
        PostPaymentRequest request = PostPaymentRequest.builder()
                .cardNumber("12345678901234")
                .expiryMonth(13)
                .expiryYear(2027)
                .currency("USD")
                .amount(1000L)
                .cvv("123")
                .build();
    
        Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(request);
    
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testRequest_expiryYearTooLarge() {
        PostPaymentRequest request = PostPaymentRequest.builder()
                .cardNumber("12345678901234")
                .expiryMonth(1)
                .expiryYear(99999)
                .currency("USD")
                .amount(1000L)
                .cvv("123")
                .build();
    
        Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(request);
    
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testRequest_expiryInThePast() {
        PostPaymentRequest request = PostPaymentRequest.builder()
                .cardNumber("12345678901234")
                .expiryMonth(1)
                .expiryYear(2020)
                .currency("USD")
                .amount(1000L)
                .cvv("123")
                .build();
    
        Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(request);
    
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testRequest_invalidCurrencyFormat() {
        PostPaymentRequest request = PostPaymentRequest.builder()
                .cardNumber("12345678901234")
                .expiryMonth(1)
                .expiryYear(2099)
                .currency("1ab")
                .amount(1000L)
                .cvv("123")
                .build();
    
        Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(request);
    
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testRequest_UnsupportedCurrency() {
        PostPaymentRequest request = PostPaymentRequest.builder()
                .cardNumber("12345678901234")
                .expiryMonth(1)
                .expiryYear(2099)
                .currency("ABC")
                .amount(1000L)
                .cvv("123")
                .build();
    
        Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(request);
    
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testRequest_InvalidAmount() {
        PostPaymentRequest request = PostPaymentRequest.builder()
                .cardNumber("12345678901234")
                .expiryMonth(1)
                .expiryYear(2099)
                .currency("USD")
                .amount(0L)
                .cvv("123")
                .build();
    
        Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(request);
    
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testRequest_InvalidCVV_InvalidLength() {
        PostPaymentRequest request = PostPaymentRequest.builder()
                .cardNumber("12345678901234")
                .expiryMonth(1)
                .expiryYear(2099)
                .currency("USD")
                .amount(1000L)
                .cvv("12345")
                .build();
    
        Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(request);
    
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testRequest_InvalidCVV_InvalidCharacter() {
        PostPaymentRequest request = PostPaymentRequest.builder()
                .cardNumber("12345678901234")
                .expiryMonth(1)
                .expiryYear(2099)
                .currency("USD")
                .amount(1000L)
                .cvv("abc")
                .build();
    
        Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(request);
    
        assertFalse(violations.isEmpty());
    }
}
