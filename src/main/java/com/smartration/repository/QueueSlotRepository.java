package com.smartration.repository;

import com.smartration.entity.Dealer;
import com.smartration.entity.QueueSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface QueueSlotRepository extends JpaRepository<QueueSlot, Long> {

    List<QueueSlot> findByDealerOrderBySlotDateAscStartTimeAsc(Dealer dealer);

    List<QueueSlot> findByDealerAndSlotDateOrderByStartTimeAsc(Dealer dealer, LocalDate slotDate);

    @Query("SELECT s FROM QueueSlot s WHERE s.dealer = :dealer AND s.slotDate = :slotDate " +
           "AND (:startTime < s.endTime AND :endTime > s.startTime)")
    List<QueueSlot> findOverlappingSlots(
            @Param("dealer") Dealer dealer,
            @Param("slotDate") LocalDate slotDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
}
