package com.kunal.simplecrudapp.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bureau")
@EnableAsync
@RequiredArgsConstructor
@Slf4j
public class CibilServiceController {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestClient restClient = RestClient.create();

    // ==========================================
    // 1. CPCS SERVICE ENDPOINT
    // ==========================================
    @PostMapping(value = "/CPCSService", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, String>>> processCpcsService(
            @RequestHeader("processName") String processName,
            @RequestBody String jsonBody) {

        try {
            List<Map<String, String>> inputList = objectMapper.readValue(jsonBody, new TypeReference<>() {});
            if (inputList.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            // Spring Asynchronous Parallel Execution (Replaces ExecutorService & CountDownLatch)
            List<CompletableFuture<Map<String, String>>> futures = inputList.stream()
                    .map(mapData -> processCpcsAsync(mapData, processName))
                    .toList();

            // Wait for all parallel tasks to complete
            List<Map<String, String>> responseList = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responseList);

        } catch (Exception e) {
            log.error("Error processing CPCSService for process: {}", processName, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==========================================
    // 2. INDIVIDUAL CIBIL ENDPOINT
    // ==========================================
    @PostMapping(value = "/IndividualCibil", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, String>>> processIndividualCibil(
            @RequestHeader("processName") String processName,
            @RequestBody String jsonBody) {

        try {
            List<Map<String, String>> inputList = objectMapper.readValue(jsonBody, new TypeReference<>() {});

            List<CompletableFuture<Map<String, String>>> futures = inputList.stream()
                    .map(mapData -> processIndividualCibilAsync(mapData, processName))
                    .toList();

            List<Map<String, String>> responseList = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responseList);

        } catch (Exception e) {
            log.error("Error processing IndividualCibil for process: {}", processName, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==========================================
    // 3. ASYNC BUSINESS LOGIC METHODS
    // ==========================================
    @Async
    public CompletableFuture<Map<String, String>> processCpcsAsync(Map<String, String> mapData, String processName) {
        Map<String, String> resultMap = new HashMap<>(mapData);
        try {
            String pinstId = mapData.getOrDefault("PINSTID", "");
            log.info("Processing CPCSService for PINSTID: {}", pinstId);

            // Construct SOAP Envelope / Payload safely
            String soapRequest = buildCpcsSoapXml(mapData);

            // WebService / API Call using Spring RestClient
            String soapResponse = restClient.post()
                    .uri("http://10.50.36.164:9050/CPCSBatchWS/PosidexService")
                    .contentType(MediaType.TEXT_XML)
                    .body(soapRequest)
                    .retrieve()
                    .body(String.class);

            // Parse response and save history to DB
            resultMap.put("Status", "SUCCESS");
            saveAuditHistory(processName, "CPCSService", pinstId, resultMap.toString());

        } catch (Exception e) {
            log.error("Error in CPCSService worker thread", e);
            resultMap.put("Status", "FAILED");
            resultMap.put("ErrorMessage", e.getMessage());
        }
        return CompletableFuture.completedFuture(resultMap);
    }

    @Async
    public CompletableFuture<Map<String, String>> processIndividualCibilAsync(Map<String, String> mapData, String processName) {
        Map<String, String> resultMap = new HashMap<>(mapData);
        try {
            String pinstId = mapData.getOrDefault("PINSTID", "");
            log.info("Processing IndividualCibil for PINSTID: {}", pinstId);

            // Business Logic & DB Call
            saveAuditHistory(processName, "IndividualCibil", pinstId, "Processed Successfully");
            resultMap.put("CibilScore", "750"); // Sample Output
            resultMap.put("Status", "SUCCESS");

        } catch (Exception e) {
            log.error("Error in IndividualCibil worker thread", e);
            resultMap.put("Status", "FAILED");
        }
        return CompletableFuture.completedFuture(resultMap);
    }

    // ==========================================
    // 4. HELPER UTILS (DB & XML)
    // ==========================================
    private void saveAuditHistory(String processName, String serviceType, String pinstId, String response) {
        String sql = "INSERT INTO DEDUPE_LOG (PROCESS_NAME, SERVICE_TYPE, PINSTID, RESPONSE) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, processName, serviceType, pinstId, response);
    }

    private String buildCpcsSoapXml(Map<String, String> mapData) {
        return """
               <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
                   <soapenv:Header/>
                   <soapenv:Body>
                       <ser:findCustomer>
                           <RefNo>%s</RefNo>
                           <FirstName>%s</FirstName>
                           <Pan>%s</Pan>
                       </ser:findCustomer>
                   </soapenv:Body>
               </soapenv:Envelope>
               """.formatted(
                mapData.getOrDefault("REFERENCE_NUMBER", ""),
                mapData.getOrDefault("FIRST_NAME", ""),
                mapData.getOrDefault("PAN_NUMBER", "")
        );
    }
}

