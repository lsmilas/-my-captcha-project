package com.example.captcha.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CaptchaController {

    private final DefaultKaptcha kaptcha;
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public CaptchaController(DefaultKaptcha kaptcha) {
        this.kaptcha = kaptcha;
    }

    // اختبار بسيط للتأكد من عمل الخدمة
    @GetMapping("/test")
    public String test() {
        return "✅ CAPTCHA Service is working!";
    }

    // إنشاء CAPTCHA جديدة
    @GetMapping("/captcha/new")
    public ResponseEntity<Map<String, String>> generateCaptcha() {
        try {
            String text = kaptcha.createText();
            BufferedImage image = kaptcha.createImage(text);
            String id = UUID.randomUUID().toString();
            
            // تحويل الصورة إلى base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            String imageBase64 = Base64.getEncoder().encodeToString(baos.toByteArray());
            
            // تخزين في الكاش
            cache.put(id, text);
            cache.put(id + "_img", imageBase64);
            
            Map<String, String> response = Map.of(
                "captchaId", id,
                "message", "CAPTCHA generated successfully"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to generate CAPTCHA: " + e.getMessage()));
        }
    }

    // الحصول على صورة CAPTCHA
    @GetMapping(value = "/captcha/image/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getCaptchaImage(@PathVariable String id) {
        try {
            String imageBase64 = cache.get(id + "_img");
            if (imageBase64 == null) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);
                
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // التحقق من الإجابة
    @PostMapping("/captcha/verify")
    public ResponseEntity<Map<String, Object>> verifyCaptcha(@RequestBody Map<String, String> request) {
        try {
            String id = request.get("id");
            String answer = request.get("answer");
            
            if (id == null || answer == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Missing id or answer"));
            }
            
            String storedText = cache.get(id);
            if (storedText == null) {
                return ResponseEntity.ok()
                    .body(Map.of("success", false, "error", "CAPTCHA expired or not found"));
            }
            
            boolean isValid = storedText.equalsIgnoreCase(answer.trim());
            
            if (isValid) {
                cache.remove(id);
                cache.remove(id + "_img");
            }
            
            return ResponseEntity.ok()
                .body(Map.of("success", isValid));
                
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // الحصول على معلومات الخدمة
    @GetMapping("/info")
    public Map<String, String> getInfo() {
        return Map.of(
            "service", "CAPTCHA Microservice",
            "version", "1.0.0",
            "status", "active",
            "endpoints", "/api/test, /api/captcha/new, /api/captcha/image/{id}, /api/captcha/verify"
        );
    }
}