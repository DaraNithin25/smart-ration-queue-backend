package com.smartration.controller;

import com.smartration.dto.DealerResponse;
import com.smartration.entity.Dealer;
import com.smartration.exception.ResourceNotFoundException;
import com.smartration.repository.DealerRepository;
import com.smartration.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dealers")
public class DealerController {

    private final DealerRepository dealerRepository;
    private final AdminService adminService;

    public DealerController(DealerRepository dealerRepository, AdminService adminService) {
        this.dealerRepository = dealerRepository;
        this.adminService = adminService;
    }

    @GetMapping
    public ResponseEntity<List<DealerResponse>> getAllDealers() {
        List<DealerResponse> dealers = dealerRepository.findAll().stream()
                .map(adminService::mapToDealerResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dealers);
    }

    @GetMapping("/{shopCode}")
    public ResponseEntity<DealerResponse> getDealerByShopCode(@PathVariable String shopCode) {
        Dealer dealer = dealerRepository.findByShopCode(shopCode)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with shop code: " + shopCode));
        return ResponseEntity.ok(adminService.mapToDealerResponse(dealer));
    }
}
