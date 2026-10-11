package com.smartration.config;

import com.smartration.entity.*;
import com.smartration.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final DealerRepository dealerRepository;
    private final RationCardRepository rationCardRepository;
    private final QueueSlotRepository queueSlotRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           DealerRepository dealerRepository,
                           RationCardRepository rationCardRepository,
                           QueueSlotRepository queueSlotRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.dealerRepository = dealerRepository;
        this.rationCardRepository = rationCardRepository;
        this.queueSlotRepository = queueSlotRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        logger.info("Checking system initialization...");

        // 1. Initialize Admin if no admin exists
        if (userRepository.countByRole(Role.ADMIN) == 0) {
            User admin = new User();
            admin.setName("System Admin");
            admin.setMobile("9000000000");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            logger.info("Initialized default ADMIN account: Mobile: 9000000000 / Password: admin123");
        } else {
            logger.info("ADMIN account already exists. Password was not modified.");
        }

        // 2. Initialize Dealer if missing
        User dealerUser;
        Optional<User> existingDealerUser = userRepository.findByMobile("9000000001");
        if (existingDealerUser.isEmpty()) {
            dealerUser = new User();
            dealerUser.setName("Ration Shop Dealer");
            dealerUser.setMobile("9000000001");
            dealerUser.setPassword(passwordEncoder.encode("dealer123"));
            dealerUser.setRole(Role.DEALER);
            dealerUser = userRepository.save(dealerUser);
            logger.info("Initialized default DEALER user: Mobile: 9000000001 / Password: dealer123");
        } else {
            dealerUser = existingDealerUser.get();
        }

        Dealer dealer;
        Optional<Dealer> existingDealer = dealerRepository.findByShopCode("FPS-HYD-001");
        if (existingDealer.isEmpty()) {
            dealer = new Dealer();
            dealer.setShopCode("FPS-HYD-001");
            dealer.setShopName("Sri Sai Ration Shop");
            dealer.setOwnerName("Ravi Kumar");
            dealer.setMobile("9876543210");
            dealer.setAddress("Shop No 4, Market Road");
            dealer.setDistrict("Hyderabad");
            dealer.setDailyCapacity(100);
            dealer.setUser(dealerUser);
            dealer = dealerRepository.save(dealer);
            logger.info("Initialized default shop: FPS-HYD-001 (Sri Sai Ration Shop)");
        } else {
            dealer = existingDealer.get();
        }

        // 3. Initialize Beneficiary if missing
        User beneficiaryUser;
        Optional<User> existingBeneficiary = userRepository.findByMobile("9876543211");
        if (existingBeneficiary.isEmpty()) {
            beneficiaryUser = new User();
            beneficiaryUser.setName("Ramesh Kumar");
            beneficiaryUser.setMobile("9876543211");
            beneficiaryUser.setPassword(passwordEncoder.encode("user123"));
            beneficiaryUser.setRole(Role.BENEFICIARY);
            beneficiaryUser = userRepository.save(beneficiaryUser);
            logger.info("Initialized default BENEFICIARY user: Mobile: 9876543211 / Password: user123");
        } else {
            beneficiaryUser = existingBeneficiary.get();
        }

        // 4. Initialize Ration Card if missing
        Optional<RationCard> existingCard = rationCardRepository.findByUser(beneficiaryUser);
        if (existingCard.isEmpty()) {
            RationCard card = new RationCard();
            card.setCardNumber("RC100001");
            card.setCardType("PHH");
            card.setFamilyMembers(4);
            card.setUser(beneficiaryUser);
            card.setDealer(dealer);
            rationCardRepository.save(card);
            logger.info("Initialized default ration card: RC100001 linked to shop FPS-HYD-001");
        }

        // 5. Create a sample upcoming queue slot if none exists for the dealer
        if (queueSlotRepository.findByDealerOrderBySlotDateAscStartTimeAsc(dealer).isEmpty()) {
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            QueueSlot slot1 = new QueueSlot();
            slot1.setDealer(dealer);
            slot1.setSlotDate(tomorrow);
            slot1.setStartTime(LocalTime.of(9, 0));
            slot1.setEndTime(LocalTime.of(9, 30));
            slot1.setCapacity(20);
            slot1.setBookedCount(0);
            queueSlotRepository.save(slot1);

            QueueSlot slot2 = new QueueSlot();
            slot2.setDealer(dealer);
            slot2.setSlotDate(tomorrow);
            slot2.setStartTime(LocalTime.of(9, 30));
            slot2.setEndTime(LocalTime.of(10, 0));
            slot2.setCapacity(20);
            slot2.setBookedCount(0);
            queueSlotRepository.save(slot2);
            logger.info("Initialized sample queue slots for dealer shop FPS-HYD-001 on " + tomorrow);
        }
    }
}
