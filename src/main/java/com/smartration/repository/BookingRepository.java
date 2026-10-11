package com.smartration.repository;

import com.smartration.entity.Booking;
import com.smartration.entity.BookingStatus;
import com.smartration.entity.QueueSlot;
import com.smartration.entity.RationCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByRationCardOrderByBookedAtDesc(RationCard rationCard);

    boolean existsByRationCardAndQueueSlotAndStatus(RationCard rationCard, QueueSlot queueSlot, BookingStatus status);

    Optional<Booking> findByTokenNumber(String tokenNumber);

    long countByQueueSlotAndStatus(QueueSlot queueSlot, BookingStatus status);

    List<Booking> findByQueueSlotOrderByBookedAtAsc(QueueSlot queueSlot);
}
