package com.smartration.controller;

import com.smartration.dto.RationCardResponse;
import com.smartration.entity.Dealer;
import com.smartration.entity.RationCard;
import com.smartration.entity.User;
import com.smartration.exception.ResourceNotFoundException;
import com.smartration.repository.RationCardRepository;
import com.smartration.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ration-cards")
public class RationCardController {

    private final RationCardRepository rationCardRepository;
    private final UserRepository userRepository;

    public RationCardController(RationCardRepository rationCardRepository, UserRepository userRepository) {
        this.rationCardRepository = rationCardRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/my-card")
    public ResponseEntity<RationCardResponse> getMyCard(Authentication authentication) {
        String mobile = authentication.getName();
        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        RationCard card = rationCardRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("No ration card assigned to this account"));

        Dealer dealer = card.getDealer();

        RationCardResponse response = new RationCardResponse(
                card.getId(),
                card.getCardNumber(),
                card.getCardType(),
                card.getFamilyMembers(),
                user.getId(),
                user.getName(),
                user.getMobile(),
                dealer != null ? dealer.getShopCode() : null,
                dealer != null ? dealer.getShopName() : null,
                dealer != null ? dealer.getAddress() : null,
                dealer != null ? dealer.getDistrict() : null
        );

        return ResponseEntity.ok(response);
    }
}
