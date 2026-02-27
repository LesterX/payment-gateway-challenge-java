# Payment Gateway

## Requirements
- JDK 17
- Docker

## Structure

src/ - Source code

test/ - JUnit tests

imposters/ - contains the bank simulator configuration. 

.editorconfig - It ensures a consistent set of rules for submissions when reformatting code

docker-compose.yml - configures the bank simulator

## How to run it
Run bank simulator first: `docker-compose up`

Run Spring-Boot service: `.\gradlew.bat bootRun`

API available will be displayed at: http://localhost:8090/swagger-ui/index.html

For unit tests, run `.\gradlew.bat test`

## API Documentation
|HTTP Method|Endpoint|Description|
|---|---|---|
|POST|/payment|Process new payment
|GET|/payment/{id}|Retrieve past payment information

### POST /payment
#### Sample Request
```
{
    "card_number": "1234567890123451",
    "expiry_month": 12,
    "expiry_year": 2030,
    "currency": "USD",
    "amount": 100,
    "cvv": "3648"
}
```

#### Input Constraint
- card_number
    - Between 14-19 characters long
    - Must only contain numeric characters
- expiry_month: Value must be between 1-12
- expiry_year: Expiry (month and year) value must be in the future
- currency
    - Must be 3 characters
    - Currently only USD, GBP, CNY are supported
- amount: Must be a positive integer
- cvv
    - Must be 3-4 characters long
    - Must only contain numeric characters
- **All fields above are required**

#### Sample Response
```
{
  "id": "17de2061-7273-4026-a5a7-28e0399aefbc",
  "status": "Authorized",
  "cardNumberLastFour": "3451",
  "expiryMonth": 12,
  "expiryYear": 2030,
  "currency": "USD",
  "amount": 100
}
```

#### Status Explanation
- Authorized: the payment was authorized by the call to the acquiring bank
- Declined: the payment was declined by the call to the acquiring bank
- Rejected: No payment could be created as invalid information was supplied to the payment gateway and therefore it has rejected the request without calling the acquiring bank

### GET /payment/{id}
`id` is required in API path. 

**No request body needed**

#### Sample Response
```
{
  "id": "17de2061-7273-4026-a5a7-28e0399aefbc",
  "status": "Authorized",
  "cardNumberLastFour": "3451",
  "expiryMonth": 12,
  "expiryYear": 2030,
  "currency": "USD",
  "amount": 100
}
```

## Design Decisions & Trade-offs
### Bean Validation vs. Service Layer Validation
Bean validation (@Valid annotation) is cleaner compared with service layer manual validation. Service layer validation can provide more customizable response body including the requested payment information. The requirement is to return REJECTED response, which doesn't say if the response needs to include data like "cardNumberLastFour". Assuming those information are not required, use Bean validation for now, otherwise we can switch to manual validation.

### Custom @AssertTrue Validation vs. Service Layer Validation
if-else statements in service layer can check whether the request expiry is in the future or not and whether the currency is supported. However, using @AssertTrue inside the model class is cleaner.

### Error Handling
#### 4xx Client Error
- GET /payment/{id} not found (non-existing ID)
- Malformed request body
- Invalid request format
    - For invalid request fields in POST /payment, API would still return the response but with `Rejected` status 

#### 5xx Server Error
- Bank service failures
- All other failures

### Unit tests
Tested the controller and validations for happy path and potential failure cases.

### Why BankClient is not tested
Unit tests on REST client with mock variables hardly provides any value.

### Why PaymentGatewayService is not tested
@SpringBootTest in controller tests already covered service layer logics.


## Assumptions
- Payment amount has to be positive.
- Only USD, CNY, GBP are supported.
- No payment data needed for rejected payment response.
- No detail information of "which field is invalid" for rejected response.


## Potential Improvements
- Support for additional currency codes
- Client authentication
- Idempotency check
- Sortable payment id
- Persist payment data before submission to bank
- Retry and timeout for bank API call
- Actual database integration