package com.example.backend.pay;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

//  토스페이먼츠 API 연동을 전담하는 서비스 (신규 생성)
@Service
public class TossPaymentsService {

    @Value("${toss.payments.secret-key}")
    private String secretKey;

    private final String TOSS_API_URL = "https://api.tosspayments.com/v1/payments/";

    /**
     * 토스페이먼츠 결제 승인 요청
     */
    public JSONObject confirmPayment(String paymentKey, String orderId, Long amount) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHeaders();
        JSONObject params = new JSONObject();
        params.put("paymentKey", paymentKey);
        params.put("orderId", orderId);
        params.put("amount", amount);

        String url = TOSS_API_URL + "confirm";
        HttpEntity<String> request = new HttpEntity<>(params.toString(), headers);

        try {
            return restTemplate.postForObject(url, request, JSONObject.class);
        } catch (Exception e) {
            throw new Exception("토스페이먼츠 결제 승인에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 토스페이먼츠 결제 취소 요청
     */
    public JSONObject cancelPayment(String paymentKey, String cancelReason) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHeaders();
        JSONObject params = new JSONObject();
        params.put("cancelReason", cancelReason);

        String url = TOSS_API_URL + paymentKey + "/cancel";
        HttpEntity<String> request = new HttpEntity<>(params.toString(), headers);


            return restTemplate.postForObject(url, request, JSONObject.class);

    }

    /**
     * 공통 헤더 생성
     */
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String encodedAuth = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        headers.setBasicAuth(encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }
}
