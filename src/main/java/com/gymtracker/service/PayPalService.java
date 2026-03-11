package com.gymtracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gymtracker.config.PayPalConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;

@Service
@Slf4j
public class PayPalService {

    private final PayPalConfig config;
    private final ObjectMapper objectMapper;

    @Autowired
    public PayPalService(PayPalConfig config, ObjectMapper objectMapper) {
        this.config = config;
        this.objectMapper = objectMapper;
    }

    private String getAccessToken() {
        String credentials = Base64.getEncoder().encodeToString(
                (config.getClientId() + ":" + config.getClientSecret()).getBytes(StandardCharsets.UTF_8)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + credentials);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        try {
            RestTemplate rest = new RestTemplate();
            ResponseEntity<String> responseEntity = rest.postForEntity(config.getBaseUrl() + "/v1/oauth2/token", request, String.class);
            String response = responseEntity.getBody();
            if (response == null) throw new RuntimeException("Empty response from PayPal token endpoint");
            JsonNode json = objectMapper.readTree(response);
            JsonNode tokenNode = json.get("access_token");
            if (tokenNode == null) throw new RuntimeException("No access_token in PayPal response: " + response);
            return tokenNode.asText();
        } catch (Exception e) {
            log.error("Failed to retrieve PayPal access token", e);
            throw new RuntimeException("Failed to retrieve PayPal access token", e);
        }
    }

    /**
     * Create a PayPal order for a subscription plan.
     *
     * @param amountCents price in cents
     * @param planName    description shown in PayPal
     * @return PayPal order ID
     */
    public String createOrder(int amountCents, String planName) {
        String token = getAccessToken();
        // Use Locale.US to ensure decimal separator is '.' (PayPal requires dot as decimal separator)
        String amount = String.format(Locale.US, "%.2f", amountCents / 100.0);

        try {
            ObjectNode orderBody = objectMapper.createObjectNode();
            orderBody.put("intent", "CAPTURE");

            ArrayNode purchaseUnits = objectMapper.createArrayNode();
            ObjectNode unit = objectMapper.createObjectNode();
            ObjectNode amountNode = objectMapper.createObjectNode();
            amountNode.put("currency_code", "USD");
            amountNode.put("value", amount);
            unit.set("amount", amountNode);
            unit.put("description", "FitZone Pro - " + planName);
            purchaseUnits.add(unit);
            orderBody.set("purchase_units", purchaseUnits);

            String body = objectMapper.writeValueAsString(orderBody);
            // Helpful debug log so body can be inspected when troubleshooting PayPal requests
            log.debug("PayPal create order body: {}", body);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            RestTemplate rest = new RestTemplate();
            ResponseEntity<String> responseEntity = rest.postForEntity(config.getBaseUrl() + "/v2/checkout/orders", entity, String.class);
            // Log the full PayPal response so the approve URL (if present) can be found in logs during testing
            log.debug("PayPal create order response: {}", responseEntity.getBody());
            String response = responseEntity.getBody();
            if (response == null) throw new RuntimeException("Empty response from PayPal create order");

            JsonNode json = objectMapper.readTree(response);
            JsonNode idNode = json.get("id");
            if (idNode == null) throw new RuntimeException("No id in PayPal create order response: " + response);
            return idNode.asText();
        } catch (Exception e) {
            log.error("PayPal create order failed", e);
            throw new RuntimeException("Failed to create PayPal order", e);
        }
    }

    /**
     * Capture a PayPal order after user approval.
     *
     * @param orderId the PayPal order ID
     * @return PayPal capture response as JSON
     */
    public JsonNode captureOrder(String orderId) {
        String token = getAccessToken();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<String> entity = new HttpEntity<>("{}", headers);
            RestTemplate rest = new RestTemplate();
            ResponseEntity<String> responseEntity = rest.postForEntity(config.getBaseUrl() + "/v2/checkout/orders/" + orderId + "/capture", entity, String.class);
            String response = responseEntity.getBody();
            if (response == null) throw new RuntimeException("Empty response from PayPal capture");

            JsonNode json = objectMapper.readTree(response);
            JsonNode statusNode = json.get("status");
            String status = statusNode != null ? statusNode.asText() : null;
            if (!"COMPLETED".equals(status)) {
                throw new IllegalStateException("PayPal capture not completed, status: " + status + " response: " + response);
            }
            return json;
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("PayPal capture order failed", e);
            throw new RuntimeException("Failed to capture PayPal order", e);
        }
    }
}
