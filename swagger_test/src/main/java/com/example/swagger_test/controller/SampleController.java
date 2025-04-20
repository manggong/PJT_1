package com.example.swagger_test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Sample API", description = "샘플 API 설명")
@RestController
@RequestMapping("/api")
public class SampleController {

    @Operation(summary = "헬로 메시지", description = "Swagger 테스트용 간단한 헬로 메시지를 반환합니다.")
    @GetMapping("/hello")
    public String hello() {
        return "Hello, Swagger!";
    }
}