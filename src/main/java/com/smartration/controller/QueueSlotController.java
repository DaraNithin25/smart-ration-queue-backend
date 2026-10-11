package com.smartration.controller;

import com.smartration.dto.SlotRequest;
import com.smartration.dto.SlotResponse;
import com.smartration.service.QueueSlotService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/queue-slots")
public class QueueSlotController {

    private final QueueSlotService queueSlotService;

    public QueueSlotController(QueueSlotService queueSlotService) {
        this.queueSlotService = queueSlotService;
    }

    @PostMapping("/dealer/{shopCode}")
    public ResponseEntity<SlotResponse> createSlot(
            @PathVariable String shopCode,
            @Valid @RequestBody SlotRequest request,
            Authentication authentication) {
        String mobile = authentication.getName();
        SlotResponse response = queueSlotService.createSlot(shopCode, request, mobile);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/dealer/{shopCode}")
    public ResponseEntity<List<SlotResponse>> getSlotsByShop(@PathVariable String shopCode) {
        List<SlotResponse> slots = queueSlotService.getSlotsByShop(shopCode);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/dealer/{shopCode}/date/{date}")
    public ResponseEntity<List<SlotResponse>> getSlotsByShopAndDate(
            @PathVariable String shopCode,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<SlotResponse> slots = queueSlotService.getSlotsByShopAndDate(shopCode, date);
        return ResponseEntity.ok(slots);
    }
}
