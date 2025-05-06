package com.micro.AuthServer.service;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.micro.AuthServer.repository.UserRepository;
import com.micro.AuthServer.util.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public UserDetailsServiceImpl(JwtUtil jwtUtil, UserRepository userRepository) {

        this.jwtUtil = jwtUtil;

        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public ResponseEntity<?> logout(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies == null) {
            throw new BadCredentialsException("No cookies present");
        }

        String refreshToken = Arrays.stream(cookies)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new BadCredentialsException("Refresh token cookie not found"));

        // String accessToken = request.getAccessToken();
        final String authHeader = httpServletRequest.getHeader("Authorization");

        String accessToken = authHeader.substring(7);

        // Optional: Validate tokens
        if (!jwtUtil.validateToken(accessToken) || !jwtUtil.validateToken(refreshToken)) {
            throw new BadCredentialsException("Invalid token(s)");
        }

        // TTL calculations
        long accessTokenTTL = calculateTTLInSeconds(jwtUtil.extractExpiration(accessToken));
        long refreshTokenTTL = calculateTTLInSeconds(jwtUtil.extractExpiration(refreshToken));

        // Blacklist both
        redisTemplate.opsForValue().set("blacklist:jwt:" + accessToken, "blacklisted", accessTokenTTL,
                TimeUnit.SECONDS);
        redisTemplate.opsForValue().set("blacklist:jwt:" + refreshToken, "blacklisted", refreshTokenTTL,
                TimeUnit.SECONDS);

        // Clear cookie
        Cookie deleteCookie = new Cookie("refreshToken", null);
        deleteCookie.setHttpOnly(true);
        // deleteCookie.setSecure(true);
        deleteCookie.setPath("/");
        deleteCookie.setMaxAge(0);
        httpServletResponse.addCookie(deleteCookie);

        return ResponseEntity.ok("Logged out successfully");
    }

    public long calculateTTLInSeconds(Date expirationDate) {
        long ttlMillis = expirationDate.getTime() - System.currentTimeMillis();
        return Math.max(ttlMillis / 1000, 0); // Prevent negative TTL
    }
}
