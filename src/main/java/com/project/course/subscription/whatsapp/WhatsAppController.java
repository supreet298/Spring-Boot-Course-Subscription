package com.project.course.subscription.whatsapp;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/whatsapp")
public class WhatsAppController {

    @Autowired
    private WhatsAppServiceImpl whatsAppService;

    @PostMapping("/sendMessage")
    public ResponseEntity<String> sendMessage(@RequestParam("to") String to, @RequestParam String message) {
        String messageSid;
        try {
            messageSid = whatsAppService.sendWhatsAppMessage(to, message);
            return ResponseEntity.ok("Message sent successfully with SID: " + messageSid);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                                 .body("Failed to send message: " + e.getMessage());
        }
    }
}


