package com.sujal.projects.airBnbApp.controller;


import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.sujal.projects.airBnbApp.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SignatureException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/webhook")
public class WebHookController {

    private final BookingService bookingService;

    @Value("${stripe.webhook.secret}")
    private String endPointSecret;

    @PostMapping(path = "/payment")
    public ResponseEntity<Void> capturePayment(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader){
        try{
            Event event = Webhook.constructEvent(payload,sigHeader,endPointSecret);
            bookingService.capturePayment(event);
            return ResponseEntity.noContent().build();
        }
        catch (SignatureVerificationException ex){
            throw new RuntimeException(ex);
        }
    }
}
