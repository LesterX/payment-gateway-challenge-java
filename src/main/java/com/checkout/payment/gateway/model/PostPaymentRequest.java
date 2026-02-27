package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.enums.SupportedCurrency;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.builder.ToStringExclude;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostPaymentRequest implements Serializable {

  @NotNull
  @Pattern(regexp = "^[0-9]{14,19}$", message = "Card number must be between 14 and 19 digits")
  @ToStringExclude
  @Schema(example = "1234567890123456")
  @JsonProperty("card_number")
  private String cardNumber;

  @NotNull
  @Min(1)
  @Max(12)
  @JsonProperty("expiry_month")
  private Integer expiryMonth;

  @NotNull
  @Min(1)
  @Max(9999)
  @Schema(example = "2030")
  @JsonProperty("expiry_year")
  private Integer expiryYear;
  
  @NotNull
  @Schema(example = "USD")
  @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3 letter code")
  private String currency;
  
  @NotNull
  @Positive
  private Long amount;
  
  @NotNull
  @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV must be a 3 or 4 digit number")
  private String cvv;

  @JsonIgnore
  @AssertTrue(message = "Expiry year and month must be valid and in the future")
  public boolean isExpiryDateFuture() {
    // If null, let @NotNull handle it.
    if (expiryYear == null || expiryMonth == null) return true;

    try {
      YearMonth expiry = YearMonth.of(expiryYear, expiryMonth);
      return expiry.isAfter(YearMonth.now());
    } catch (DateTimeException e) {
        return false;
    }
  }

  @JsonIgnore
  @AssertTrue(message = "Currency code is not supported")
  public boolean isSupportedCurrency() {
    if (StringUtils.isBlank(currency)) {
      return false;
    }

    try {
      SupportedCurrency.valueOf(currency);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  @JsonIgnore
  public String getExpiryString() {
    return YearMonth.of(expiryYear, expiryMonth).format(DateTimeFormatter.ofPattern("MM/yyyy"));
  }
}
