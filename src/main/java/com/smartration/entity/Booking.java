package com.smartration.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_number", nullable = false, unique = true)
    private String tokenNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ration_card_id", nullable = false)
    private RationCard rationCard;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "queue_slot_id", nullable = false)
    private QueueSlot queueSlot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.BOOKED;

    @Column(name = "booked_at", nullable = false)
    private LocalDateTime bookedAt;

    public Booking() {
    }

    public Booking(Long id, String tokenNumber, RationCard rationCard, QueueSlot queueSlot, BookingStatus status, LocalDateTime bookedAt) {
        this.id = id;
        this.tokenNumber = tokenNumber;
        this.rationCard = rationCard;
        this.queueSlot = queueSlot;
        this.status = status;
        this.bookedAt = bookedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(String tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public RationCard getRationCard() {
        return rationCard;
    }

    public void setRationCard(RationCard rationCard) {
        this.rationCard = rationCard;
    }

    public QueueSlot getQueueSlot() {
        return queueSlot;
    }

    public void setQueueSlot(QueueSlot queueSlot) {
        this.queueSlot = queueSlot;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public void setBookedAt(LocalDateTime bookedAt) {
        this.bookedAt = bookedAt;
    }
}
