package com.example.swagger_test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Sample API", description = "샘플 API 설명")
@RestController
@RequestMapping("/api")
@NoArgsConstructor
public class SampleController {

    private final Logger log = LoggerFactory.getLogger(SampleController.class);

    @Operation(summary = "헬로 메시지", description = "Swagger 테스트용 간단한 헬로 메시지를 반환합니다.")
    @GetMapping("/hello")
    public String hello() {
        log.info("hello");
        return "Hello, Swagger!";
    }

    @Operation(summary = "post test", description = "test post api")
    @PostMapping("/post")
    public String post(@RequestBody Map<String, String> reqParams) {
        StringBuilder sb = new StringBuilder();

        reqParams.forEach((k, v) -> {sb.append(k).append("=").append(v).append("; ");});

        return sb.toString();
    }

    @GetMapping("/response-entity")
    public ResponseEntity<String> responseEntity() {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("ok!");
    }
}