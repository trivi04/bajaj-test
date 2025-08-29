package com.example.bfhq;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@SpringBootApplication
public class BfhqApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BfhqApplication.class, args);
    }

    @Override
    public void run(String... args) {
        RestTemplate rest = new RestTemplate();

        // 1. Candidate info
        Map<String, String> body = Map.of(
                "name", "John Doe",
                "regNo", "REG12347",
                "email", "john@example.com"
        );

        // 2. Call generateWebhook
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        ResponseEntity<Map> response = rest.postForEntity(url, body, Map.class);
        String webhook = (String) response.getBody().get("webhook");
        String token = (String) response.getBody().get("accessToken");

        // 3. Pick SQL
        String regNo = body.get("regNo");
        int lastTwo = Integer.parseInt(regNo.replaceAll("\\D+", "").substring(regNo.length()-2));
        String sql = (lastTwo % 2 == 1) ? "SELECT * FROM table_q1;" : "SELECT * FROM table_q2;";

        // 4. Submit answer
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token); // if 401, try "Bearer " + token
        HttpEntity<Map<String, String>> req = new HttpEntity<>(Map.of("finalQuery", sql), headers);
        ResponseEntity<String> result = rest.exchange(webhook, HttpMethod.POST, req, String.class);

        System.out.println("Submission response: " + result.getBody());
    }
}
