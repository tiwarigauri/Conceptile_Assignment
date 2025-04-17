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

If you face any issues or need further assistance, please don’t hesitate to reach out. Thank you for your time and effort in reviewing this assignment!









1. Folder Setup (Recommended Quick Structure)

Place everything under src/test/resources/ or your appropriate test folder:

- src/test/resources/
  - public_key.pem     <-- Store your public key file here
  - jwt-utils/
      - JwtUtils.java  <-- Developer’s methods here


---

2. Developer's Code (JwtUtils.java)

Create this class (if not already created) and place all provided methods in it. Also, add loadPublicKey() method here:

public class JwtUtils {

    public static PublicKey loadPublicKey(String filename) throws Exception {
        InputStream is = JwtUtils.class.getClassLoader().getResourceAsStream(filename);
        String publicKeyPEM = new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"))
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static boolean verifyJwtToken(String token, PublicKey publicKey) {
        // Developer's logic to verify token signature
    }

    public static boolean isTokenExpired(String token) {
        // Developer's logic to check 'exp' claim
    }

    public static Map<String, Object> decodedJwtToken(String token) {
        // Developer's logic to decode payload claims
    }
}


---

3. Modified Java Method (Quick Test of All 12 Test Cases)

Extend your existing CheckBvDIDandURL() method:

public boolean CheckBvDIDandURL() throws Exception {
    boolean status = false;
    Thread.sleep(5000);
    seleniumutils.switchToFrame("frmDetails");

    // Frontend checks
    boolean btn = seleniumutils.webElementOf("AlertEnrichmentPage.tabLaunchSanctions360").isDisplayed();
    JavascriptExecutor executor = (JavascriptExecutor) Context.global().getDriver();
    String BVDID = (String) executor.executeScript("return window._amcBvdId;");
    String moodysURL = (String) executor.executeScript("return window.moodysOrbisUrl;");

    Hooks.scenario.log("Generated bvdId: " + BVDID);
    Hooks.scenario.log("Generated Moody's URL: " + moodysURL);

    if (BVDID == null || BVDID.isEmpty() || moodysURL == null || moodysURL.isEmpty()) {
        Hooks.scenario.log("Missing BVDID or Moody URL");
        return true;
    }

    // Extract Token from URL
    String token = moodysURL.split("token=")[1].split("&")[0];
    PublicKey publicKey = JwtUtils.loadPublicKey("public_key.pem");

    // 1. Verify JWT Signature
    if (!JwtUtils.verifyJwtToken(token, publicKey)) {
        Hooks.scenario.log("Token signature is invalid");
        status = true;
    }

    // 2. Decode Payload
    Map<String, Object> payload = JwtUtils.decodedJwtToken(token);
    long iat = ((Number) payload.get("iat")).longValue();
    long nbf = ((Number) payload.get("nbf")).longValue();
    long exp = ((Number) payload.get("exp")).longValue();
    long now = Instant.now().getEpochSecond();

    // 3. Token not expired
    if (now > exp) {
        Hooks.scenario.log("Token is expired");
        status = true;
    }

    // 4. 'iat' claim present
    if (iat <= 0) {
        Hooks.scenario.log("'iat' is not set properly");
        status = true;
    }

    // 5. 'nbf' within 30 seconds before iat
    if (nbf < iat - 30 || nbf > iat) {
        Hooks.scenario.log("'nbf' is not within 30 seconds before 'iat'");
        status = true;
    }

    // 6. 'exp' is 30-120 seconds after 'iat'
    long diff = exp - iat;
    if (diff < 30 || diff > 120) {
        Hooks.scenario.log("'exp' is not between 30 and 120 seconds after 'iat'");
        status = true;
    }

    // 7. Validate 'iss'
    if (!"expected_issuer".equals(payload.get("iss"))) {
        Hooks.scenario.log("'iss' claim is invalid");
        status = true;
    }

    // 8. Validate 'aud'
    if (!"expected_audience".equals(payload.get("aud"))) {
        Hooks.scenario.log("'aud' claim is invalid");
        status = true;
    }

    // 9. Validate 'sub'
    if (payload.get("sub") == null || ((String) payload.get("sub")).isEmpty()) {
        Hooks.scenario.log("'sub' is missing or empty");
        status = true;
    }

    // 10. Optional: 'name' claim
    if (payload.containsKey("name") && ((String) payload.get("name")).isEmpty()) {
        Hooks.scenario.log("'name' is empty when present");
        status = true;
    }

    // 11. Optional: 'email' claim
    if (payload.containsKey("email") && ((String) payload.get("email")).isEmpty()) {
        Hooks.scenario.log("'email' is empty when present");
        status = true;
    }

    // 12. Optional: 'jti' claim
    if (payload.containsKey("jti") && ((String) payload.get("jti")).isEmpty()) {
        Hooks.scenario.log("'jti' is empty when present");
        status = true;
    }

    // 13. Header validation - decode header
    String[] parts = token.split("\\.");
    String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
    JSONObject header = new JSONObject(headerJson);

    if (!"RS256".equals(header.getString("alg")) || !"JWT".equals(header.getString("typ")) || header.getString("kid") == null) {
        Hooks.scenario.log("Header claims 'alg', 'typ' or 'kid' are incorrect");
        status = true;
    }

    return status;
}


---

4. Summary of What You Did Here

Reused your method and enhanced it to validate JWT test cases.

Used developer’s methods from a shared JwtUtils class.

Loaded the public key from resources/public_key.pem.

Validated all fields and claims per the test cases in Excel.



---

5. Next Step (Organizing Later)

Separate each test case into its own scenario in a .feature file.

Create step definition methods for each validation.

Reuse JwtUtils and Hooks.scenario.log() for logging.
