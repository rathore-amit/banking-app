package com.bank.gateway.filter;

import com.bank.gateway.config.JwtUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 📍 Concept: "Security Deep-Dive" notebook — simplified login for demo purposes.
 * Production mein ye ek proper Auth Service (DB-backed users, BCrypt password check)
 * hota — yahan demo ke liye hardcoded users hain taaki poora flow chalke dikha sakein.
 */
@RestController
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/api/auth/login")
    public Map<String, String> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        // DEMO ONLY — real system mein BCrypt.matches() aur DB lookup hoga
        if ("alice".equals(username) && "password123".equals(password)) {
            return Map.of("token", jwtUtil.generateToken("alice", "CUSTOMER"));
        }
        if ("admin".equals(username) && "adminpass".equals(password)) {
            return Map.of("token", jwtUtil.generateToken("admin", "ADMIN"));
        }
        throw new RuntimeException("Invalid credentials");
    }
}
