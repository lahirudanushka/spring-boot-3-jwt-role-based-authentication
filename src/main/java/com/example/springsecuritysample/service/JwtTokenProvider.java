package com.example.springsecuritysample.service;

import com.example.springsecuritysample.dto.LoginRequest;
import com.example.springsecuritysample.repository.User;
import com.example.springsecuritysample.repository.UserRepository;
import com.example.springsecuritysample.util.CommonException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtTokenProvider {


    @Autowired
    private UserRepository userRepository;

    @Value("${jwt-secret}")
    private String secretKey = "your-secret-key";

    @Value("${jwt-exp-time-in-ms}")
    private long exp;


    public String generateToken(LoginRequest body) {

        User user = userRepository.findByUsername(body.getUserName());

        if (user == null) {
            throw new CommonException("403", "User " + body.getUserName() + " not found", HttpStatus.FORBIDDEN, null);
        }

        if (!BCrypt.checkpw(body.getPassword(), user.getPassword())) {
            throw new CommonException("403", "Incorrect password", HttpStatus.FORBIDDEN, null);
        }


        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("session_id", UUID.randomUUID().toString());

        Header header = Jwts.header();
        header.setType("access");
        return Jwts.builder()
                .setHeader((Map<String, Object>) header)
                .setClaims(claims)
                .setSubject(body.getUserName())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + exp))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    public boolean validateToken(String token) {
        return new Date().before(extractExpiration(token));
    }
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <Y> Y extractClaimWithType(String token, String claimName, Class<Y> returnClass) {
        return extractAllClaims(token).get(claimName, returnClass);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody();
    }

}
