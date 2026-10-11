package com.smartration.repository;

import com.smartration.entity.Dealer;
import com.smartration.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, Long> {
    Optional<Dealer> findByShopCode(String shopCode);
    Optional<Dealer> findByUserId(Long userId);
    Optional<Dealer> findByUser(User user);
    boolean existsByShopCode(String shopCode);
}
