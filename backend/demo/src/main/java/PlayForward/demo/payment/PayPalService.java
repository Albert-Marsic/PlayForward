package PlayForward.demo.payment;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
public class PayPalService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;
    private final String clientId;
    private final String clientSecret;

    public PayPalService(@Value("${app.paypal.base-url:https://api-m.sandbox.paypal.com}") String baseUrl,
                         @Value("${app.paypal.client-id:}") String clientId,
                         @Value("${app.paypal.client-secret:}") String clientSecret) {
        this.baseUrl = trimTrailingSlash(baseUrl);
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public PayPalOrder createOrder(BigDecimal amount, String description) {
        String token = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String amountValue = formatAmount(amount);
        Map<String, Object> payload = Map.of(
                "intent", "CAPTURE",
                "purchase_units", List.of(Map.of(
                        "amount", Map.of(
                                "currency_code", "EUR",
                                "value", amountValue
                        ),
                        "description", description
                )),
                "application_context", Map.of("shipping_preference", "NO_SHIPPING")
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                baseUrl + "/v2/checkout/orders",
                entity,
                JsonNode.class
        );

        JsonNode body = requireBody(response);
        String id = textOrNull(body.get("id"));
        if (id == null) {
            throw new IllegalStateException("PayPal order id is missing in response");
        }
        String status = textOrNull(body.get("status"));
        return new PayPalOrder(id, status);
    }

    public PayPalCapture captureOrder(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("PayPal order id is required");
        }

        String token = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(Map.of(), headers);
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                baseUrl + "/v2/checkout/orders/" + orderId + "/capture",
                entity,
                JsonNode.class
        );

        JsonNode body = requireBody(response);
        String status = textOrNull(body.get("status"));
        JsonNode amountNode = body.path("purchase_units").path(0)
                .path("payments").path("captures").path(0).path("amount");
        BigDecimal amount = parseAmount(amountNode.path("value"));
        String currency = textOrNull(amountNode.get("currency_code"));
        return new PayPalCapture(orderId, status, amount, currency);
    }

    private String getAccessToken() {
        if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
            throw new IllegalStateException("PayPal client credentials are missing");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                baseUrl + "/v1/oauth2/token",
                entity,
                JsonNode.class
        );

        JsonNode responseBody = requireBody(response);
        String token = textOrNull(responseBody.get("access_token"));
        if (token == null) {
            throw new IllegalStateException("PayPal access token is missing in response");
        }
        return token;
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount is required");
        }
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private BigDecimal parseAmount(JsonNode node) {
        String value = textOrNull(node);
        if (value == null) {
            return null;
        }
        return new BigDecimal(value);
    }

    private JsonNode requireBody(ResponseEntity<JsonNode> response) {
        if (response == null || !response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("PayPal request failed");
        }
        JsonNode body = response.getBody();
        if (body == null) {
            throw new IllegalStateException("PayPal response body is empty");
        }
        return body;
    }

    private String textOrNull(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        String value = node.asText();
        return (value == null || value.isBlank()) ? null : value;
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String trimmed = value.trim();
        return trimmed.endsWith("/") ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
    }

    public static final class PayPalOrder {
        public final String id;
        public final String status;

        public PayPalOrder(String id, String status) {
            this.id = id;
            this.status = status;
        }
    }

    public static final class PayPalCapture {
        public final String orderId;
        public final String status;
        public final BigDecimal amount;
        public final String currency;

        public PayPalCapture(String orderId, String status, BigDecimal amount, String currency) {
            this.orderId = orderId;
            this.status = status;
            this.amount = amount;
            this.currency = currency;
        }
    }
}
