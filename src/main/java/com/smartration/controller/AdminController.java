package com.smartration.controller;

import com.smartration.dto.*;
import com.smartration.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/dealers")
    public ResponseEntity<DealerResponse> createDealer(@Valid @RequestBody CreateDealerRequest request) {
        DealerResponse response = adminService.createDealer(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/link-dealer")
    public ResponseEntity<DealerResponse> linkDealerToShop(@Valid @RequestBody LinkShopRequest request) {
        DealerResponse response = adminService.linkDealerToShop(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dealers")
    public ResponseEntity<List<DealerResponse>> getAllDealers() {
        List<DealerResponse> dealers = adminService.getAllDealers();
        return ResponseEntity.ok(dealers);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        Map<String, Object> stats = adminService.getSystemStats();
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/assign-card")
    public ResponseEntity<RationCardResponse> assignRationCard(@Valid @RequestBody AssignCardRequest request) {
        RationCardResponse response = adminService.assignRationCard(request);
        return ResponseEntity.ok(response);
    }
}
