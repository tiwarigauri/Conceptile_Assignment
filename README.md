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


















String token = MoodysURL.split("token=")[1].split("&")[0];
PublicKey publicKey = loadPublicKeyFromJKS("src/test/resources/your-key.jks", "yourAlias", "yourPassword");

try {
    // 1. Signature validation
    JwtUtils.verifyJwtToken(token, publicKey);

    // 2. Decode token
    DecodedJWT decodedJWT = JwtUtils.decodedJwtToken(token);

    // 3. Expiry validation
    if (JwtUtils.isTokenExpired(token)) {
        Hooks.scenario.log("Token is expired");
    }

    // 4. iat validation
    Date iat = decodedJWT.getIssuedAt();
    if (iat == null) {
        Hooks.scenario.log("iat claim is missing or invalid");
    }

    // 5. nbf within 30s before iat
    Date nbf = decodedJWT.getNotBefore();
    if (nbf != null && iat != null) {
        long secondsDiff = (iat.getTime() - nbf.getTime()) / 1000;
        if (secondsDiff < 0 || secondsDiff > 30) {
            Hooks.scenario.log("nbf is not within 30s before iat");
        }
    }

    // 6. exp 30-120s after iat
    Date exp = decodedJWT.getExpiresAt();
    if (exp != null && iat != null) {
        long secondsDiff = (exp.getTime() - iat.getTime()) / 1000;
        if (secondsDiff < 30 || secondsDiff > 120) {
            Hooks.scenario.log("exp is not 30-120s after iat");
        }
    }

    // 7. iss validation
    String iss = decodedJWT.getIssuer();
    if (!"expectedIssuer".equals(iss)) {
        Hooks.scenario.log("Invalid iss claim");
    }

    // 8. aud validation
    String aud = decodedJWT.getAudience().get(0);
    if (!"expectedAudience".equals(aud)) {
        Hooks.scenario.log("Invalid aud claim");
    }

    // 9. sub validation
    if (decodedJWT.getSubject() == null || decodedJWT.getSubject().isEmpty()) {
        Hooks.scenario.log("Invalid sub claim");
    }

    // 10. name/email/jti validation (optional claims)
    String name = decodedJWT.getClaim("name").asString();
    String email = decodedJWT.getClaim("email").asString();
    String jti = decodedJWT.getClaim("jti").asString();

    if (name != null && name.trim().isEmpty()) Hooks.scenario.log("Empty name");
    if (email != null && email.trim().isEmpty()) Hooks.scenario.log("Empty email");
    if (jti != null && jti.trim().isEmpty()) Hooks.scenario.log("Empty jti");

    // 11. Header validations
    if (!"JWT".equals(decodedJWT.getType())) Hooks.scenario.log("Invalid typ");
    if (!"ES256".equals(decodedJWT.getAlgorithm())) Hooks.scenario.log("Invalid alg");
    if (decodedJWT.getKeyId() == null) Hooks.scenario.log("Missing kid");

} catch (Exception e) {
    Hooks.scenario.log("JWT validation failed: " + e.getMessage());
    e.printStackTrace();
}
