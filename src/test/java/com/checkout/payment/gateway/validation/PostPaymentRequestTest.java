package com.checkout.payment.gateway.validation;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.checkout.payment.gateway.model.PostPaymentRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.assertj.core.api.Assertions.assertThat;

public class PostPaymentRequestTest {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void whenAllFieldsValidThenNoViolations() {
        PostPaymentRequest request = PostPaymentRequest.builder()
                .cardNumber("12345678901234")
                .expiryMonth(12)
                .expiryYear(2099)
                .currency("USD")
                .amount(1000L)
                .cvv("123")
                .build();
    
        Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(request);
    
        assertTrue(violations.isEmpty());
        assertEquals("12/2099", request.getExpiryString());
    }

    @Test
    public void whenFieldsMissingThenViolationsReturned() {
        PostPaymentRequest request = PostPaymentRequest.builder().build();
    
        Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(request);
    
        Set<String> paths = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());
        assertThat(paths).contains("cardNumber", "currency", "amount", "expiryMonth", "expiryYear", "cvv");
    }

    @Test
    public void whenCardNumberTooShortThenViolationReturned() {
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

        Set<String> paths = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());
        assertThat(paths).contains("cardNumber");
    }

    @Test
    public void whenCardNumberContainsNonNumericThenViolationReturned() {
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
        Set<String> paths = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());
        assertThat(paths).contains("cardNumber");
    }

    @Test
    public void whenExpiryMonthOutOfRangeThenViolationReturned() {
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
        Set<String> paths = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());
        assertThat(paths).contains("expiryMonth");
    }

    @Test
    public void whenExpiryYearOutOfRangeThenViolationReturned() {
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

        Set<String> paths = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());
        assertThat(paths).contains("expiryYear");
    }

    @Test
    public void whenExpiryDateInThePastThenViolationReturned() {
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

        Set<String> paths = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());
        assertThat(paths).contains("expiryDateFuture");
    }

    @Test
    public void whenCurrencyFormatInvalidThenViolationReturned() {
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

        Set<String> paths = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());
        assertThat(paths).contains("currency");
    }

    @Test
    public void whenCurrencyNotSupportedThenViolationReturned() {
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

        Set<String> paths = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());
        assertThat(paths).contains("supportedCurrency");
    }

    @Test
    public void whenAmountNotPositiveThenViolationReturned() {
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

        Set<String> paths = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());
        assertThat(paths).contains("amount");
    }

    @Test
    public void whenCvvLengthInvalidThenViolationReturned() {
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

        Set<String> paths = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());
        assertThat(paths).contains("cvv");
    }

    @Test
    public void whenCvvContainsNonNumericThenViolationReturned() {
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

        Set<String> paths = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());
        assertThat(paths).contains("cvv");
    }
}
