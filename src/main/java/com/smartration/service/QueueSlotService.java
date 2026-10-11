package com.smartration.service;

import com.smartration.dto.SlotRequest;
import com.smartration.dto.SlotResponse;
import com.smartration.entity.Dealer;
import com.smartration.entity.QueueSlot;
import com.smartration.entity.Role;
import com.smartration.entity.User;
import com.smartration.exception.BadRequestException;
import com.smartration.exception.ResourceNotFoundException;
import com.smartration.repository.DealerRepository;
import com.smartration.repository.QueueSlotRepository;
import com.smartration.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QueueSlotService {

    private final QueueSlotRepository queueSlotRepository;
    private final DealerRepository dealerRepository;
    private final UserRepository userRepository;

    public QueueSlotService(QueueSlotRepository queueSlotRepository,
                            DealerRepository dealerRepository,
                            UserRepository userRepository) {
        this.queueSlotRepository = queueSlotRepository;
        this.dealerRepository = dealerRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SlotResponse createSlot(String shopCode, SlotRequest request, String authenticatedMobile) {
        Dealer dealer = dealerRepository.findByShopCode(shopCode)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with shop code: " + shopCode));

        User currentUser = userRepository.findByMobile(authenticatedMobile)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        // Only the linked dealer or admin can create slots for this shop
        if (currentUser.getRole() != Role.ADMIN) {
            if (dealer.getUser() == null || !dealer.getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Only the linked dealer can create slots for shop: " + shopCode);
            }
        }

        // Validate capacity
        if (request.getCapacity() <= 0) {
            throw new BadRequestException("Capacity must be positive and greater than 0");
        }

        // Validate time
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new BadRequestException("End time must be after start time");
        }

        // Validate duration is 30 minutes
        long minutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
        if (minutes != 30) {
            throw new BadRequestException("Slot duration must be exactly 30 minutes (got " + minutes + " minutes)");
        }

        // Validate date is not in the past
        LocalDate today = LocalDate.now();
        if (request.getSlotDate().isBefore(today)) {
            throw new BadRequestException("Cannot create queue slots for past dates");
        }

        if (request.getSlotDate().isEqual(today) && request.getStartTime().isBefore(LocalTime.now())) {
            throw new BadRequestException("Cannot create queue slots for a past time today");
        }

        // Prevent overlapping slots for the same shop
        List<QueueSlot> overlapping = queueSlotRepository.findOverlappingSlots(
                dealer, request.getSlotDate(), request.getStartTime(), request.getEndTime()
        );
        if (!overlapping.isEmpty()) {
            throw new BadRequestException("Slot time overlaps with an existing slot for this shop on " + request.getSlotDate());
        }

        QueueSlot slot = new QueueSlot();
        slot.setDealer(dealer);
        slot.setSlotDate(request.getSlotDate());
        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setCapacity(request.getCapacity());
        slot.setBookedCount(0);

        QueueSlot saved = queueSlotRepository.save(slot);
        return mapToResponse(saved);
    }

    public List<SlotResponse> getSlotsByShop(String shopCode) {
        Dealer dealer = dealerRepository.findByShopCode(shopCode)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with shop code: " + shopCode));

        return queueSlotRepository.findByDealerOrderBySlotDateAscStartTimeAsc(dealer)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<SlotResponse> getSlotsByShopAndDate(String shopCode, LocalDate date) {
        Dealer dealer = dealerRepository.findByShopCode(shopCode)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with shop code: " + shopCode));

        return queueSlotRepository.findByDealerAndSlotDateOrderByStartTimeAsc(dealer, date)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public SlotResponse mapToResponse(QueueSlot slot) {
        int available = Math.max(0, slot.getCapacity() - slot.getBookedCount());
        return new SlotResponse(
                slot.getId(),
                slot.getSlotDate(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getCapacity(),
                slot.getBookedCount(),
                available,
                slot.getDealer().getShopCode(),
                slot.getDealer().getShopName()
        );
    }
}
