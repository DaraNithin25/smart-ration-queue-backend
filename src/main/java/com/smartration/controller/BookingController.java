package com.smartration.controller;

import com.smartration.dto.BookingResponse;
import com.smartration.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/{slotId}")
    public ResponseEntity<BookingResponse> bookSlot(
            @PathVariable Long slotId,
            Authentication authentication) {
        String mobile = authentication.getName();
        BookingResponse response = bookingService.bookSlot(slotId, mobile);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings(Authentication authentication) {
        String mobile = authentication.getName();
        List<BookingResponse> bookings = bookingService.getMyBookings(mobile);
        return ResponseEntity.ok(bookings);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long bookingId,
            Authentication authentication) {
        String mobile = authentication.getName();
        BookingResponse response = bookingService.cancelBooking(bookingId, mobile);
        return ResponseEntity.ok(response);
    }
}
