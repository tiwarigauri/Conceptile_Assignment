# README

Thank you for reviewing this assignment! This guide will walk you through setting up, running, and testing the application. Please follow the steps below to ensure a smooth testing experience.

---

## Prerequisites

- **Java 11** or higher installed on your system.
- **Maven** installed for building and running the application.
- **Postman** or any other API testing tool for testing the APIs.

---

## Getting Started

### Step 1: Start the Application

1. Clone the repository to your local system.
2. Navigate to the project directory in your terminal.
3. Run the following command to start the application:
   ```bash
   mvn spring-boot:run
   ```
4. Ensure the application is running at `http://localhost:8080`.

---

## Testing the APIs

### API 1: Start a New Quiz Session

**Endpoint:**
- **URL:** `POST http://localhost:8080/api/quiz/start`
- **Request Body:** None

**Expected Response:**
```json
{
  "id": 1,
  "totalQuestions": 0,
  "correctAnswers": 0,
  "incorrectAnswers": 0
}
```

**Steps to Test:**
1. Open Postman.
2. Create a new request.
3. Set the method to `POST`.
4. Enter the URL `http://localhost:8080/api/quiz/start`.
5. Click **Send** and verify the response.

---

### API 2: Get a Random Question

**Endpoint:**
- **URL:** `GET http://localhost:8080/api/quiz/question`
- **Request Body:** None

**Expected Response:**
```json
{
  "questionId": 1,
  "questionText": "What is the capital of France?",
  "optionA": "Paris",
  "optionB": "London",
  "optionC": "Berlin",
  "optionD": "Madrid"
}
```

**Steps to Test:**
1. Create a new request in Postman.
2. Set the method to `GET`.
3. Enter the URL `http://localhost:8080/api/quiz/question`.
4. Click **Send** and verify the response.

---

### API 3: Submit an Answer and Get the total questions answered by the user with details on correct and incorrect submission

**Endpoint:**
- **URL:** `POST http://localhost:8080/api/quiz/submit`
- **Request Body:**
```json
{
  "sessionId": 1,
  "questionId": 1,
  "selectedAnswer": "A"
}
```

**Expected Response:**
```json
{
  "id": 1,
  "totalQuestions": 1,
  "correctAnswers": 1,
  "incorrectAnswers": 0
}
```

**Steps to Test:**
1. Create a new request in Postman.
2. Set the method to `POST`.
3. Enter the URL `http://localhost:8080/api/quiz/submit`.
4. Go to the **Body** tab, select **raw**, and set the format to **JSON**.
5. Paste the request body and click **Send**.

---

## Verifying Data in the H2 Database

1. Open your browser and navigate to `http://localhost:8080/h2-console`.
2. Use the following credentials:
   - **JDBC URL:** `jdbc:h2:mem:testdb`
   - **Username:** `sa`
   - **Password:** `password`
3. Click **Connect**.
4. Query the `quiz_session` or `question` tables to verify the data.
   ```sql
   SELECT * FROM quiz_session;
   SELECT * FROM question;
   ```

---

## Need Help?
























Feature: JWT Token Validation

  Scenario: Verify JWT Token is not expired
    Given the JWT token is extracted from the URL
    When the token is decoded
    Then the token should not be expired

  Scenario: Verify 'iat' claim is correctly set
    Given the JWT token is extracted from the URL
    When the token is decoded
    Then the 'iat' claim should be present and valid

  Scenario: Verify 'nbf' claim is within 30 seconds before 'iat'
    Given the JWT token is extracted from the URL
    When the token is decoded
    Then the 'nbf' claim should be within 30 seconds before 'iat'

  Scenario: Verify 'exp' claim is 30-120 seconds after 'iat'
    Given the JWT token is extracted from the URL
    When the token is decoded
    Then the 'exp' claim should be 30 to 120 seconds after 'iat'

  Scenario: Verify 'iss' claim is set correctly
    Given the JWT token is extracted from the URL
    When the token is decoded
    Then the 'iss' claim should be "trusted-issuer"

  Scenario: Verify 'aud' claim is set correctly
    Given the JWT token is extracted from the URL
    When the token is decoded
    Then the 'aud' claim should be "intended-audience"

  Scenario: Verify 'sub' claim is a valid ASCII of length 8
    Given the JWT token is extracted from the URL
    When the token is decoded
    Then the 'sub' claim should be a valid 8-character ASCII string

  Scenario: Verify optional 'name' claim is either valid or omitted
    Given the JWT token is extracted from the URL
    When the token is decoded
    Then the 'name' claim should be valid if present

  Scenario: Verify optional 'email' claim is either valid or omitted
    Given the JWT token is extracted from the URL
    When the token is decoded
    Then the 'email' claim should be valid if present

  Scenario: Verify optional 'jti' claim is either valid or omitted
    Given the JWT token is extracted from the URL
    When the token is decoded
    Then the 'jti' claim should be valid if present

  Scenario: Verify 'alg', 'typ', and 'kid' headers are set correctly
    Given the JWT token is extracted from the URL
    When the token is decoded
    Then the header fields 'alg', 'typ', and 'kid' should be correctly set

  Scenario: Verify JWT token signature is valid
    Given the JWT token is extracted from the URL
    When the public key is loaded
    Then the token signature should be valid















package fast.common.glue;

import com.citi.utils.JwtUtils; import com.auth0.jwt.interfaces.DecodedJWT; import io.cucumber.java.en.; import java.security.PublicKey; import static org.junit.Assert.;

public class JWTTokenStepDefinition {

private String token;
private DecodedJWT decodedJWT;
private PublicKey publicKey;

@Given("the JWT token is extracted from the URL")
public void extractToken() {
    // Mock extraction logic - replace with real logic
    token = "your.jwt.token.here";
}

@When("the token is decoded")
public void decodeToken() {
    decodedJWT = JwtUtils.decodedJwtToken(token);
}

@When("the public key is loaded")
public void loadPublicKey() throws Exception {
    publicKey = JwtUtils.loadPublicKey("keys/public_key.pem");
}

@Then("the token should not be expired")
public void validateExpiration() {
    assertFalse("Token is expired", JwtUtils.isTokenExpired(token));
}

@Then("the 'iat' claim should be present and valid")
public void validateIat() {
    assertNotNull("'iat' claim is missing", decodedJWT.getIssuedAt());
}

@Then("the 'nbf' claim should be within 30 seconds before 'iat'")
public void validateNbf() {
    JwtUtils.validateNbf(decodedJWT);
}

@Then("the 'exp' claim should be 30 to 120 seconds after 'iat'")
public void validateExp() {
    JwtUtils.validateExp(decodedJWT);
}

@Then("the 'iss' claim should be \"trusted-issuer\"")
public void validateIssuer() {
    assertEquals("trusted-issuer", decodedJWT.getIssuer());
}

@Then("the 'aud' claim should be \"intended-audience\"")
public void validateAudience() {
    assertEquals("intended-audience", decodedJWT.getAudience().get(0));
}

@Then("the 'sub' claim should be a valid 8-character ASCII string")
public void validateSub() {
    JwtUtils.validateSub(decodedJWT);
}

@Then("the 'name' claim should be valid if present")
public void validateName() {
    JwtUtils.validateOptionalClaim(decodedJWT, "name");
}

@Then("the 'email' claim should be valid if present")
public void validateEmail() {
    JwtUtils.validateOptionalClaim(decodedJWT, "email");
}

@Then("the 'jti' claim should be valid if present")
public void validateJti() {
    JwtUtils.validateOptionalClaim(decodedJWT, "jti");
}

@Then("the header fields 'alg', 'typ', and 'kid' should be correctly set")
public void validateHeaders() {
    JwtUtils.validateHeaders(decodedJWT);
}

@Then("the token signature should be valid")
public void validateSignature() {
    JwtUtils.verifyJwtToken(token, publicKey);
}
}













package fast.common.pages;



import com.auth0.jwt.interfaces.DecodedJWT;

import org.openqa.selenium.JavascriptExecutor;

import org.openqa.selenium.WebDriver;

import fast.common.utils.JwtUtils;



import java.security.PublicKey;

import java.util.Date;



public class AlertEnrichmentPage {



    WebDriver driver = Context.global().getDriver();



    public String extractTokenFromMoodyUrl() {

        JavascriptExecutor executor = (JavascriptExecutor) driver;

        String moodysUrl = (String) executor.executeScript("return window.moodysOrbisUrl;");

        if (moodysUrl != null && moodysUrl.contains("token=")) {

            return moodysUrl.split("token=")[1];

        }

        return null;

    }



    public DecodedJWT decodeJwtToken(String token) throws Exception {

        return JwtUtils.decodedJwtToken(token);

    }



    public boolean isTokenExpired(String token) {

        return JwtUtils.isTokenExpired(token);

    }



    public boolean isIatPresent(DecodedJWT decodedJWT) {

        return decodedJWT.getIssuedAt() != null;

    }



    public boolean isNbfValid(DecodedJWT decodedJWT) {

        Date iat = decodedJWT.getIssuedAt();

        Date nbf = decodedJWT.getNotBefore();

        if (iat == null || nbf == null) return false;

        long diffInSeconds = (iat.getTime() - nbf.getTime()) / 1000;

        return diffInSeconds <= 30;

    }



    public boolean isExpValid(DecodedJWT decodedJWT) {

        Date iat = decodedJWT.getIssuedAt();

        Date exp = decodedJWT.getExpiresAt();

        if (iat == null || exp == null) return false;

        long diffInSeconds = (exp.getTime() - iat.getTime()) / 1000;

        return diffInSeconds >= 30 && diffInSeconds <= 120;

    }



    public boolean isIssuerValid(DecodedJWT decodedJWT) {

        return "expected_issuer".equals(decodedJWT.getIssuer());

    }



    public boolean isAudienceValid(DecodedJWT decodedJWT) {

        return decodedJWT.getAudience().contains("expected_audience");

    }



    public boolean isSubValid(DecodedJWT decodedJWT) {

        String sub = decodedJWT.getSubject();

        if (sub == null || sub.length() != 8) return false;

        for (char ch : sub.toCharArray()) {

            if (ch < 32 || ch > 126) return false;

        }

        return true;

    }



    public boolean isNameClaimValid(DecodedJWT decodedJWT) {

        String name = decodedJWT.getClaim("name").asString();

        return name == null || !name.isEmpty();

    }



    public boolean isEmailClaimValid(DecodedJWT decodedJWT) {

        String email = decodedJWT.getClaim("email").asString();

        return email == null || !email.isEmpty();

    }



    public boolean isJtiClaimValid(DecodedJWT decodedJWT) {

        String jti = decodedJWT.getClaim("jti").asString();

        return jti == null || !jti.isEmpty();

    }



    public boolean areHeaderFieldsValid(DecodedJWT decodedJWT) {

        return "ES256".equals(decodedJWT.getAlgorithm())

                && "JWT".equals(decodedJWT.getType())

                && decodedJWT.getKeyId() != null;

    }



    public PublicKey getPublicKey() throws Exception {

        return JwtUtils.loadPublicKey("keys/public_key.pem");  // or use .jks logic if needed

    }



    public void verifyTokenSignature(String token, PublicKey publicKey) {

        JwtUtils.verifyJwtToken(token, publicKey);

    }

}
