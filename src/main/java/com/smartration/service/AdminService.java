package com.smartration.service;

import com.smartration.dto.AssignCardRequest;
import com.smartration.dto.CreateDealerRequest;
import com.smartration.dto.DealerResponse;
import com.smartration.dto.LinkShopRequest;
import com.smartration.dto.RationCardResponse;
import com.smartration.entity.Dealer;
import com.smartration.entity.RationCard;
import com.smartration.entity.Role;
import com.smartration.entity.User;
import com.smartration.exception.BadRequestException;
import com.smartration.exception.ResourceNotFoundException;
import com.smartration.repository.BookingRepository;
import com.smartration.repository.DealerRepository;
import com.smartration.repository.QueueSlotRepository;
import com.smartration.repository.RationCardRepository;
import com.smartration.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final DealerRepository dealerRepository;
    private final RationCardRepository rationCardRepository;
    private final QueueSlotRepository queueSlotRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepository,
                        DealerRepository dealerRepository,
                        RationCardRepository rationCardRepository,
                        QueueSlotRepository queueSlotRepository,
                        BookingRepository bookingRepository,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.dealerRepository = dealerRepository;
        this.rationCardRepository = rationCardRepository;
        this.queueSlotRepository = queueSlotRepository;
        this.bookingRepository = bookingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public DealerResponse createDealer(CreateDealerRequest request) {
        String mobile = request.getMobile().trim();
        String shopCode = request.getShopCode().trim().toUpperCase();

        if (userRepository.existsByMobile(mobile)) {
            throw new BadRequestException("User already exists with mobile: " + mobile);
        }
        if (dealerRepository.existsByShopCode(shopCode)) {
            throw new BadRequestException("Dealer shop code already exists: " + shopCode);
        }

        // 1. Create User account with DEALER role
        User dealerUser = new User();
        dealerUser.setName(request.getName().trim());
        dealerUser.setMobile(mobile);
        dealerUser.setPassword(passwordEncoder.encode(request.getPassword()));
        dealerUser.setRole(Role.DEALER);
        User savedUser = userRepository.save(dealerUser);

        // 2. Create Dealer profile
        Dealer dealer = new Dealer();
        dealer.setShopCode(shopCode);
        dealer.setShopName(request.getShopName().trim());
        dealer.setOwnerName(request.getOwnerName() != null ? request.getOwnerName().trim() : request.getName().trim());
        dealer.setMobile(mobile);
        dealer.setDistrict(request.getDistrict() != null ? request.getDistrict().trim() : "Main District");
        dealer.setAddress(request.getAddress() != null ? request.getAddress().trim() : "Shop Address");
        dealer.setDailyCapacity(request.getDailyCapacity() != null ? request.getDailyCapacity() : 100);
        dealer.setUser(savedUser);

        Dealer savedDealer = dealerRepository.save(dealer);
        return mapToDealerResponse(savedDealer);
    }

    @Transactional
    public DealerResponse linkDealerToShop(LinkShopRequest request) {
        String shopCode = request.getShopCode().trim().toUpperCase();
        Dealer dealer = dealerRepository.findByShopCode(shopCode)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer shop not found with code: " + shopCode));

        User user = userRepository.findByMobile(request.getMobile().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Dealer user not found with mobile: " + request.getMobile()));

        // Ensure user has DEALER role
        if (user.getRole() != Role.DEALER) {
            user.setRole(Role.DEALER);
            userRepository.save(user);
        }

        dealer.setUser(user);
        dealer.setMobile(user.getMobile());
        Dealer updated = dealerRepository.save(dealer);
        return mapToDealerResponse(updated);
    }

    public List<DealerResponse> getAllDealers() {
        return dealerRepository.findAll().stream()
                .map(this::mapToDealerResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RationCardResponse assignRationCard(AssignCardRequest request) {
        String mobile = request.getMobile().trim();
        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary user not found with mobile: " + mobile));

        Dealer dealer = dealerRepository.findByShopCode(request.getShopCode().trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with code: " + request.getShopCode()));

        RationCard card = rationCardRepository.findByUser(user).orElse(new RationCard());
        card.setCardNumber(request.getCardNumber().trim().toUpperCase());
        card.setCardType(request.getCardType() != null ? request.getCardType() : "PHH");
        card.setFamilyMembers(request.getFamilyMembers() != null ? request.getFamilyMembers() : 4);
        card.setUser(user);
        card.setDealer(dealer);

        RationCard saved = rationCardRepository.save(card);
        return new RationCardResponse(
                saved.getId(),
                saved.getCardNumber(),
                saved.getCardType(),
                saved.getFamilyMembers(),
                user.getId(),
                user.getName(),
                user.getMobile(),
                dealer.getShopCode(),
                dealer.getShopName(),
                dealer.getAddress(),
                dealer.getDistrict()
        );
    }

    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalDealers", dealerRepository.count());
        stats.put("totalSlots", queueSlotRepository.count());
        stats.put("totalBookings", bookingRepository.count());
        return stats;
    }

    public DealerResponse mapToDealerResponse(Dealer d) {
        return new DealerResponse(
                d.getId(),
                d.getShopCode(),
                d.getShopName(),
                d.getOwnerName(),
                d.getMobile(),
                d.getAddress(),
                d.getDistrict(),
                d.getDailyCapacity(),
                d.getUser() != null ? d.getUser().getId() : null,
                d.getUser() != null ? d.getUser().getName() : null
        );
    }
}
