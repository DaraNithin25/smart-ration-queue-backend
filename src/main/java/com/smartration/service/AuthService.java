package com.smartration.service;

import com.smartration.config.JwtTokenProvider;
import com.smartration.dto.AuthRequest;
import com.smartration.dto.AuthResponse;
import com.smartration.dto.RegisterRequest;
import com.smartration.entity.Dealer;
import com.smartration.entity.RationCard;
import com.smartration.entity.Role;
import com.smartration.entity.User;
import com.smartration.exception.BadRequestException;
import com.smartration.repository.DealerRepository;
import com.smartration.repository.RationCardRepository;
import com.smartration.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final DealerRepository dealerRepository;
    private final RationCardRepository rationCardRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       DealerRepository dealerRepository,
                       RationCardRepository rationCardRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.dealerRepository = dealerRepository;
        this.rationCardRepository = rationCardRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByMobile(request.getMobile().trim())
                .orElseThrow(() -> new BadCredentialsException("Invalid mobile number or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid mobile number or password");
        }

        String token = tokenProvider.generateToken(user);

        String shopCode = null;
        if (user.getRole() == Role.DEALER) {
            Optional<Dealer> dealerOpt = dealerRepository.findByUserId(user.getId());
            if (dealerOpt.isPresent()) {
                shopCode = dealerOpt.get().getShopCode();
            }
        } else if (user.getRole() == Role.BENEFICIARY) {
            Optional<RationCard> cardOpt = rationCardRepository.findByUserId(user.getId());
            if (cardOpt.isPresent() && cardOpt.get().getDealer() != null) {
                shopCode = cardOpt.get().getDealer().getShopCode();
            }
        }

        return new AuthResponse(
                token,
                user.getRole().name(),
                user.getName(),
                user.getMobile(),
                user.getId(),
                shopCode
        );
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String mobile = request.getMobile().trim();
        if (userRepository.existsByMobile(mobile)) {
            throw new BadRequestException("Mobile number is already registered: " + mobile);
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setMobile(mobile);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.BENEFICIARY);
        User savedUser = userRepository.save(user);

        // Auto-link a ration card for smooth demonstration
        String cardNumber = (request.getCardNumber() != null && !request.getCardNumber().trim().isEmpty())
                ? request.getCardNumber().trim()
                : "RC" + mobile;

        // Ensure unique card number
        if (rationCardRepository.existsByCardNumber(cardNumber)) {
            cardNumber = "RC" + System.currentTimeMillis();
        }

        Dealer assignedDealer = null;
        if (request.getShopCode() != null && !request.getShopCode().trim().isEmpty()) {
            assignedDealer = dealerRepository.findByShopCode(request.getShopCode().trim()).orElse(null);
        }
        if (assignedDealer == null) {
            // Assign to default first dealer if exists
            assignedDealer = dealerRepository.findAll().stream().findFirst().orElse(null);
        }

        RationCard card = new RationCard();
        card.setCardNumber(cardNumber);
        card.setCardType("PHH");
        card.setFamilyMembers(4);
        card.setUser(savedUser);
        card.setDealer(assignedDealer);
        rationCardRepository.save(card);

        String token = tokenProvider.generateToken(savedUser);

        return new AuthResponse(
                token,
                savedUser.getRole().name(),
                savedUser.getName(),
                savedUser.getMobile(),
                savedUser.getId(),
                assignedDealer != null ? assignedDealer.getShopCode() : null
        );
    }
}
