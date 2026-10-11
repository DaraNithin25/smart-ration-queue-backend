package com.smartration.repository;

import com.smartration.entity.Dealer;
import com.smartration.entity.RationCard;
import com.smartration.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RationCardRepository extends JpaRepository<RationCard, Long> {
    Optional<RationCard> findByUserId(Long userId);
    Optional<RationCard> findByUser(User user);
    Optional<RationCard> findByCardNumber(String cardNumber);
    boolean existsByCardNumber(String cardNumber);
    List<RationCard> findByDealer(Dealer dealer);
}
