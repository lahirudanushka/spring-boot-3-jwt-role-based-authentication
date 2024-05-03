package com.example.springsecuritysample.controller;

import com.example.springsecuritysample.dto.LoginRequest;
import com.example.springsecuritysample.service.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1")
public class SampleController {


    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/test")
    ResponseEntity<String> sampleEndpoint() {
        return ResponseEntity.ok("Successfully Authenticated");
    }

    @PostMapping(value = "/token", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest body) {
        String token = jwtTokenProvider.generateToken(body);
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }

    @PostMapping(value = "/refresh-token", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Map<String, String>> refresh(HttpServletRequest httpServletRequest) {
        String token = jwtTokenProvider.refresh(httpServletRequest);
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }
}
