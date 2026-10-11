package com.smartration.service;

import com.smartration.dto.BookingResponse;
import com.smartration.entity.*;
import com.smartration.exception.BadRequestException;
import com.smartration.exception.ResourceNotFoundException;
import com.smartration.repository.BookingRepository;
import com.smartration.repository.QueueSlotRepository;
import com.smartration.repository.RationCardRepository;
import com.smartration.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final QueueSlotRepository queueSlotRepository;
    private final RationCardRepository rationCardRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository,
                          QueueSlotRepository queueSlotRepository,
                          RationCardRepository rationCardRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.queueSlotRepository = queueSlotRepository;
        this.rationCardRepository = rationCardRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookingResponse bookSlot(Long slotId, String authenticatedMobile) {
        User beneficiaryUser = userRepository.findByMobile(authenticatedMobile)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated beneficiary user not found"));

        RationCard rationCard = rationCardRepository.findByUser(beneficiaryUser)
                .orElseThrow(() -> new BadRequestException("No ration card linked to your account. Please link your ration card first."));

        QueueSlot slot = queueSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue slot not found with ID: " + slotId));

        // Rule: The ration card must be linked to the same shop as the selected slot
        if (rationCard.getDealer() == null || !rationCard.getDealer().getId().equals(slot.getDealer().getId())) {
            String shopName = slot.getDealer() != null ? slot.getDealer().getShopName() : "this shop";
            throw new BadRequestException("Your ration card is not registered with " + shopName + " (" + slot.getDealer().getShopCode() + ").");
        }

        // Rule: Prevent booking past dates/times
        LocalDate today = LocalDate.now();
        if (slot.getSlotDate().isBefore(today)) {
            throw new BadRequestException("Cannot book a queue slot for a past date");
        }
        if (slot.getSlotDate().isEqual(today) && slot.getStartTime().isBefore(LocalTime.now())) {
            throw new BadRequestException("Cannot book a queue slot that has already passed today");
        }

        // Rule: Prevent duplicate active booking for the same slot
        boolean alreadyBookedThisSlot = bookingRepository.existsByRationCardAndQueueSlotAndStatus(
                rationCard, slot, BookingStatus.BOOKED
        );
        if (alreadyBookedThisSlot) {
            throw new BadRequestException("You already have an active booking for this queue slot");
        }

        // Rule: Do not allow bookings above capacity
        if (slot.getBookedCount() >= slot.getCapacity()) {
            throw new BadRequestException("This slot is fully booked. Queue capacity (" + slot.getCapacity() + ") reached.");
        }

        // Increment bookedCount safely
        slot.setBookedCount(slot.getBookedCount() + 1);
        queueSlotRepository.save(slot);

        // Generate unique token number: TKN-<SHOP_CODE>-<YYYYMMDD>-<SEQ>
        String dateStr = slot.getSlotDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String cleanShopCode = slot.getDealer().getShopCode().replaceAll("[^a-zA-Z0-9]", "");
        String tokenNumber = generateUniqueToken(cleanShopCode, dateStr, slot.getBookedCount());

        Booking booking = new Booking();
        booking.setTokenNumber(tokenNumber);
        booking.setRationCard(rationCard);
        booking.setQueueSlot(slot);
        booking.setStatus(BookingStatus.BOOKED);
        booking.setBookedAt(LocalDateTime.now());

        Booking saved = bookingRepository.save(booking);
        return mapToResponse(saved);
    }

    public List<BookingResponse> getMyBookings(String authenticatedMobile) {
        User beneficiaryUser = userRepository.findByMobile(authenticatedMobile)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        RationCard rationCard = rationCardRepository.findByUser(beneficiaryUser)
                .orElseThrow(() -> new ResourceNotFoundException("No ration card found for user"));

        return bookingRepository.findByRationCardOrderByBookedAtDesc(rationCard)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId, String authenticatedMobile) {
        User beneficiaryUser = userRepository.findByMobile(authenticatedMobile)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        // Rule: A beneficiary can only cancel their own booking
        if (!booking.getRationCard().getUser().getId().equals(beneficiaryUser.getId()) && beneficiaryUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You are not authorized to cancel this booking");
        }

        // Rule: Prevent cancelling an already cancelled booking
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("This booking is already cancelled");
        }

        // Mark cancelled
        booking.setStatus(BookingStatus.CANCELLED);

        // Decrease bookedCount safely
        QueueSlot slot = booking.getQueueSlot();
        if (slot != null && slot.getBookedCount() > 0) {
            slot.setBookedCount(slot.getBookedCount() - 1);
            queueSlotRepository.save(slot);
        }

        Booking saved = bookingRepository.save(booking);
        return mapToResponse(saved);
    }

    private String generateUniqueToken(String shopCode, String dateStr, int count) {
        String token = String.format("TKN-%s-%s-%03d", shopCode, dateStr, count);
        // Ensure uniqueness in case of collision
        if (bookingRepository.findByTokenNumber(token).isPresent()) {
            token = String.format("TKN-%s-%s-%s", shopCode, dateStr, UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        }
        return token;
    }

    public BookingResponse mapToResponse(Booking booking) {
        QueueSlot slot = booking.getQueueSlot();
        RationCard card = booking.getRationCard();
        Dealer dealer = slot != null ? slot.getDealer() : (card != null ? card.getDealer() : null);
        User user = card != null ? card.getUser() : null;

        return new BookingResponse(
                booking.getId(),
                booking.getTokenNumber(),
                booking.getStatus().name(),
                booking.getBookedAt(),
                slot != null ? slot.getId() : null,
                slot != null ? slot.getSlotDate() : null,
                slot != null ? slot.getStartTime() : null,
                slot != null ? slot.getEndTime() : null,
                card != null ? card.getCardNumber() : null,
                card != null ? card.getCardType() : null,
                user != null ? user.getName() : null,
                user != null ? user.getMobile() : null,
                dealer != null ? dealer.getShopCode() : null,
                dealer != null ? dealer.getShopName() : null,
                dealer != null ? dealer.getAddress() : null
        );
    }
}
