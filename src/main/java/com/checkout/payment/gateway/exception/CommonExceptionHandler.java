package com.checkout.payment.gateway.exception;

import com.checkout.payment.gateway.model.ErrorResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(CommonExceptionHandler.class);

  @ExceptionHandler(EventProcessingException.class)
  public ResponseEntity<ErrorResponse> handleException(EventProcessingException ex) {
    LOG.error("Exception happened", ex);
    return new ResponseEntity<>(new ErrorResponse("Not found"),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(BankServiceException.class)
  public ResponseEntity<ErrorResponse> handleBankServiceException(BankServiceException ex) {
    LOG.error("Exception from acquring bank", ex);
    return new ResponseEntity<>(new ErrorResponse("Unable to process the request due to bank service issue"),
      HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleMalformedRequest(HttpMessageNotReadableException ex) {
    LOG.warn("Malformed request body", ex);
    return new ResponseEntity<>(new ErrorResponse("Invalid request body"),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception ex) {
    LOG.error("Unknown exception", ex);
    return new ResponseEntity<>(new ErrorResponse("Something went wrong"), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
