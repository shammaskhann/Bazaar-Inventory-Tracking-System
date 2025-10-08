//package com.example.internshipproject.InventoryManagementV2.core.utils;
//
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import java.time.Duration;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//public class RateLimitInterceptor implements HandlerInterceptor {
//
//    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
//
//    private Bucket createNewBucket() {
//        return Bucket4j.builder()
//                .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)))) // 5 requests per minute
//                .build();
//    }
//
//    private Bucket resolveBucket(HttpServletRequest request) {
//        String ip = request.getRemoteAddr(); // Rate limit per IP
//        return cache.computeIfAbsent(ip, k -> createNewBucket());
//    }
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler) {
//        Bucket bucket = resolveBucket(request);
//        if (bucket.tryConsume(1)) {
//            return true; // Allow the request
//        } else {
//            response.setStatus(429); // Too Many Requests
//            response.getWriter().write("Too many requests. Please try again later.");
//            return false;
//        }
//    }
//}
//
