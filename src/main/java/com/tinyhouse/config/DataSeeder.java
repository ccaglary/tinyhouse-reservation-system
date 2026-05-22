package com.tinyhouse.config;

import com.tinyhouse.entity.*;
import com.tinyhouse.enums.*;
import com.tinyhouse.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedData(UserRepository userRepo,
                               TinyHouseRepository houseRepo,
                               TinyHouseImageRepository imageRepo,
                               ReservationRepository reservationRepo,
                               ReviewRepository reviewRepo) {
        return args -> {
            if (userRepo.count() > 0) {
                log.info("Database already seeded, skipping...");
                return;
            }

            log.info("=== Seeding Database with Sample Data ===");

            User admin = userRepo.save(User.builder()
                    .firstName("Admin").lastName("TinyHouse")
                    .email("admin@tinyhouse.com")
                    .password(passwordEncoder.encode("admin123"))
                    .phoneNumber("05551234567")
                    .role(Role.ADMIN).active(true).emailVerified(true).build());

            User owner1 = userRepo.save(User.builder()
                    .firstName("Ahmet").lastName("Yılmaz")
                    .email("ahmet@tinyhouse.com")
                    .password(passwordEncoder.encode("123456"))
                    .phoneNumber("05359876543")
                    .role(Role.OWNER).active(true).emailVerified(true).build());

            User owner2 = userRepo.save(User.builder()
                    .firstName("Fatma").lastName("Demir")
                    .email("fatma@tinyhouse.com")
                    .password(passwordEncoder.encode("123456"))
                    .phoneNumber("05423456789")
                    .role(Role.OWNER).active(true).emailVerified(true).build());

            User tenant1 = userRepo.save(User.builder()
                    .firstName("Mehmet").lastName("Kaya")
                    .email("mehmet@tinyhouse.com")
                    .password(passwordEncoder.encode("123456"))
                    .phoneNumber("05301112233")
                    .role(Role.TENANT).active(true).emailVerified(true).build());

            User tenant2 = userRepo.save(User.builder()
                    .firstName("Elif").lastName("Çelik")
                    .email("elif@tinyhouse.com")
                    .password(passwordEncoder.encode("123456"))
                    .phoneNumber("05444455566")
                    .role(Role.TENANT).active(true).emailVerified(true).build());

            TinyHouse h1 = houseRepo.save(TinyHouse.builder()
                    .title("Boğaz Manzaralı Modern Tiny House")
                    .description("İstanbul Boğazı'nın eşsiz manzarasına sahip, modern tasarımlı tiny house. Tam donanımlı mutfak, rahat yatak ve geniş teras ile unutulmaz bir konaklama deneyimi sunar.")
                    .city("İstanbul").district("Beykoz").address("Beykoz Korusu Yolu No:15")
                    .latitude(41.1325).longitude(29.0965)
                    .nightlyPrice(new BigDecimal("850")).cleaningFee(new BigDecimal("150"))
                    .capacity(4).roomCount(2).bedCount(2).bathroomCount(1)
                    .wifi(true).parking(true).airConditioner(true).petAllowed(false)
                    .averageRating(new BigDecimal("4.80"))
                    .active(true).owner(owner1).build());

            TinyHouse h2 = houseRepo.save(TinyHouse.builder()
                    .title("Adalar Manzaralı Sahil Evi")
                    .description("Prens Adaları manzarasına bakan, sahile sıfır konumda romantik tiny house. Deniz sesi eşliğinde huzurlu bir tatil için ideal.")
                    .city("İstanbul").district("Maltepe").address("Sahil Yolu Cad. No:42")
                    .latitude(40.9332).longitude(29.1355)
                    .nightlyPrice(new BigDecimal("720")).cleaningFee(new BigDecimal("100"))
                    .capacity(2).roomCount(1).bedCount(1).bathroomCount(1)
                    .wifi(true).parking(false).airConditioner(true).petAllowed(true)
                    .averageRating(new BigDecimal("4.60"))
                    .active(true).owner(owner1).build());

            TinyHouse h3 = houseRepo.save(TinyHouse.builder()
                    .title("Olimpos Orman Evi")
                    .description("Olimpos'un eşsiz doğasında, çam ormanları arasında konumlanmış rustic tarzda tiny house. Antik kent yürüyüş mesafesinde.")
                    .city("Antalya").district("Kumluca").address("Olimpos Yolu Km:3")
                    .latitude(36.3957).longitude(30.4728)
                    .nightlyPrice(new BigDecimal("550")).cleaningFee(new BigDecimal("75"))
                    .capacity(3).roomCount(1).bedCount(2).bathroomCount(1)
                    .wifi(true).parking(true).airConditioner(false).petAllowed(true)
                    .averageRating(new BigDecimal("4.90"))
                    .active(true).owner(owner2).build());

            TinyHouse h4 = houseRepo.save(TinyHouse.builder()
                    .title("Kaş Deniz Manzaralı Villa Tiny")
                    .description("Kaş'ın turkuaz sularına bakan, özel havuzlu lüks tiny house. Akdeniz mimarisinden esinlenilmiş tasarımıyla göz kamaştırıyor.")
                    .city("Antalya").district("Kaş").address("Çukurbağ Yarımadası No:8")
                    .latitude(36.2001).longitude(29.6389)
                    .nightlyPrice(new BigDecimal("1200")).cleaningFee(new BigDecimal("200"))
                    .capacity(4).roomCount(2).bedCount(2).bathroomCount(2)
                    .wifi(true).parking(true).airConditioner(true).petAllowed(false)
                    .averageRating(new BigDecimal("4.70"))
                    .active(true).owner(owner2).build());

            TinyHouse h5 = houseRepo.save(TinyHouse.builder()
                    .title("Fethiye Ölüdeniz Dağ Evi")
                    .description("Ölüdeniz'in ünlü lagününe tepeden bakan, panoramik manzaralı dağ evi. Yamaç paraşütü pistine yakın konumda.")
                    .city("Muğla").district("Fethiye").address("Babadağ Eteği Köyiçi No:23")
                    .latitude(36.5501).longitude(29.1167)
                    .nightlyPrice(new BigDecimal("680")).cleaningFee(new BigDecimal("100"))
                    .capacity(2).roomCount(1).bedCount(1).bathroomCount(1)
                    .wifi(true).parking(true).airConditioner(true).petAllowed(false)
                    .averageRating(new BigDecimal("4.85"))
                    .active(true).owner(owner1).build());

            TinyHouse h6 = houseRepo.save(TinyHouse.builder()
                    .title("Bodrum Taş Ev Tiny House")
                    .description("Bodrum'un tarihi dokusuna uygun taş mimarisiyle inşa edilmiş butik tiny house. Gümüşlük sahiline yürüme mesafesinde.")
                    .city("Muğla").district("Bodrum").address("Gümüşlük Mah. Sahil Sk. No:7")
                    .latitude(37.0519).longitude(27.2281)
                    .nightlyPrice(new BigDecimal("950")).cleaningFee(new BigDecimal("150"))
                    .capacity(3).roomCount(1).bedCount(2).bathroomCount(1)
                    .wifi(true).parking(false).airConditioner(true).petAllowed(true)
                    .averageRating(new BigDecimal("4.50"))
                    .active(true).owner(owner2).build());

            TinyHouse h7 = houseRepo.save(TinyHouse.builder()
                    .title("Abant Gölü Orman Kaçamağı")
                    .description("Abant Gölü manzaralı, kış-yaz her mevsim keyifli bir kaçamak noktası. Şömineli oturma odası ve geniş balkon.")
                    .city("Bolu").district("Mudurnu").address("Abant Tabiat Parkı Girişi")
                    .latitude(40.6013).longitude(31.2735)
                    .nightlyPrice(new BigDecimal("480")).cleaningFee(new BigDecimal("75"))
                    .capacity(4).roomCount(2).bedCount(2).bathroomCount(1)
                    .wifi(true).parking(true).airConditioner(false).petAllowed(true)
                    .averageRating(new BigDecimal("4.75"))
                    .active(true).owner(owner1).build());

            TinyHouse h8 = houseRepo.save(TinyHouse.builder()
                    .title("Alaçatı Rüzgâr Evi")
                    .description("Alaçatı'nın renkli sokaklarında, yel değirmenlerine yakın konumda şirin bir tiny house. Sörf için mükemmel rüzgâr koşulları.")
                    .city("İzmir").district("Çeşme").address("Alaçatı Mahallesi 3001 Sk. No:12")
                    .latitude(38.2783).longitude(26.3731)
                    .nightlyPrice(new BigDecimal("780")).cleaningFee(new BigDecimal("100"))
                    .capacity(2).roomCount(1).bedCount(1).bathroomCount(1)
                    .wifi(true).parking(true).airConditioner(true).petAllowed(false)
                    .averageRating(new BigDecimal("4.65"))
                    .active(true).owner(owner2).build());

            TinyHouse h9 = houseRepo.save(TinyHouse.builder()
                    .title("Uzungöl Yayla Evi")
                    .description("Uzungöl'ün büyüleyici manzarasına sahip, geleneksel Karadeniz mimarisinde ahşap tiny house. Çay bahçeleri arasında huzur.")
                    .city("Trabzon").district("Çaykara").address("Uzungöl Mah. Göl Kenarı")
                    .latitude(40.6118).longitude(40.2900)
                    .nightlyPrice(new BigDecimal("420")).cleaningFee(new BigDecimal("50"))
                    .capacity(3).roomCount(1).bedCount(2).bathroomCount(1)
                    .wifi(true).parking(true).airConditioner(false).petAllowed(true)
                    .averageRating(new BigDecimal("4.55"))
                    .active(true).owner(owner1).build());

            TinyHouse h10 = houseRepo.save(TinyHouse.builder()
                    .title("Kapadokya Peribacası Tiny House")
                    .description("Göreme'nin eşsiz peri bacalarına bakan, kaya oyma tasarımlı modern tiny house. Sabah balon seyri için mükemmel konum.")
                    .city("Nevşehir").district("Göreme").address("Göreme Kasabası Müze Cad. No:5")
                    .latitude(38.6431).longitude(34.8289)
                    .nightlyPrice(new BigDecimal("900")).cleaningFee(new BigDecimal("125"))
                    .capacity(2).roomCount(1).bedCount(1).bathroomCount(1)
                    .wifi(true).parking(true).airConditioner(true).petAllowed(false)
                    .averageRating(new BigDecimal("4.95"))
                    .active(true).owner(owner2).build());

            TinyHouse h11 = houseRepo.save(TinyHouse.builder()
                    .title("Sapanca Göl Kenarı Tiny House")
                    .description("Sapanca Gölü'nün sakin sularına bakan, yeşillikler içinde huzurlu tiny house. Barbekü alanı ve göl manzaralı veranda.")
                    .city("Sakarya").district("Sapanca").address("Göl Kenarı Mevkii No:18")
                    .latitude(40.6919).longitude(30.2711)
                    .nightlyPrice(new BigDecimal("520")).cleaningFee(new BigDecimal("80"))
                    .capacity(4).roomCount(2).bedCount(2).bathroomCount(1)
                    .wifi(true).parking(true).airConditioner(false).petAllowed(true)
                    .averageRating(new BigDecimal("4.40"))
                    .active(true).owner(owner1).build());

            TinyHouse h12 = houseRepo.save(TinyHouse.builder()
                    .title("Akçakoca Sahil Tiny House")
                    .description("Akçakoca'nın gizli koylarından birine bakan, deniz kokulu tiny house. Fındık bahçeleri arasında doğa ile baş başa kalın.")
                    .city("Düzce").district("Akçakoca").address("Sahil Mah. Koy Yolu No:3")
                    .latitude(41.0877).longitude(31.1145)
                    .nightlyPrice(new BigDecimal("380")).cleaningFee(new BigDecimal("60"))
                    .capacity(2).roomCount(1).bedCount(1).bathroomCount(1)
                    .wifi(false).parking(true).airConditioner(false).petAllowed(true)
                    .averageRating(new BigDecimal("4.30"))
                    .active(true).owner(owner2).build());

            log.info("12 Tiny Houses created across 9 cities");

            String[] imageUrls = {
                "https://images.unsplash.com/photo-1587061949409-02df41d5e562?w=800&h=500&fit=crop",
                "https://images.unsplash.com/photo-1588880331179-bc9b93a8cb5e?w=800&h=500&fit=crop",
                "https://images.unsplash.com/photo-1510798831971-661eb04b3739?w=800&h=500&fit=crop",
                "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?w=800&h=500&fit=crop",
                "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=800&h=500&fit=crop",
                "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=800&h=500&fit=crop",
                "https://images.unsplash.com/photo-1542718610-a1d656d1884c?w=800&h=500&fit=crop",
                "https://images.unsplash.com/photo-1523217582562-09d0def993a6?w=800&h=500&fit=crop",
                "https://images.unsplash.com/photo-1540518614846-7eded433c457?w=800&h=500&fit=crop",
                "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800&h=500&fit=crop",
                "https://images.unsplash.com/photo-1480074568708-e7b720bb3f09?w=800&h=500&fit=crop",
                "https://images.unsplash.com/photo-1449824913935-59a10b8d2000?w=800&h=500&fit=crop",
            };
            TinyHouse[] houses = {h1, h2, h3, h4, h5, h6, h7, h8, h9, h10, h11, h12};
            for (int i = 0; i < houses.length; i++) {
                imageRepo.save(TinyHouseImage.builder()
                        .imageUrl(imageUrls[i])
                        .coverImage(true)
                        .tinyHouse(houses[i]).build());
            }

            LocalDate d1Start = LocalDate.now().plusDays(5);
            LocalDate d1End = LocalDate.now().plusDays(8);
            reservationRepo.save(Reservation.builder()
                    .tinyHouse(h1).tenant(tenant1)
                    .startDate(d1Start).endDate(d1End)
                    .totalDays((int) ChronoUnit.DAYS.between(d1Start, d1End))
                    .totalPrice(new BigDecimal("2700"))
                    .reservationStatus(ReservationStatus.CONFIRMED)
                    .paymentStatus(PaymentStatus.COMPLETED)
                    .build());

            LocalDate d2Start = LocalDate.now().plusDays(10);
            LocalDate d2End = LocalDate.now().plusDays(14);
            reservationRepo.save(Reservation.builder()
                    .tinyHouse(h3).tenant(tenant2)
                    .startDate(d2Start).endDate(d2End)
                    .totalDays((int) ChronoUnit.DAYS.between(d2Start, d2End))
                    .totalPrice(new BigDecimal("2275"))
                    .reservationStatus(ReservationStatus.PENDING)
                    .paymentStatus(PaymentStatus.PENDING)
                    .build());

            LocalDate d3Start = LocalDate.now().minusDays(10);
            LocalDate d3End = LocalDate.now().minusDays(7);
            reservationRepo.save(Reservation.builder()
                    .tinyHouse(h5).tenant(tenant1)
                    .startDate(d3Start).endDate(d3End)
                    .totalDays((int) ChronoUnit.DAYS.between(d3Start, d3End))
                    .totalPrice(new BigDecimal("2140"))
                    .reservationStatus(ReservationStatus.COMPLETED)
                    .paymentStatus(PaymentStatus.COMPLETED)
                    .build());

            LocalDate d4Start = LocalDate.now().plusDays(20);
            LocalDate d4End = LocalDate.now().plusDays(25);
            reservationRepo.save(Reservation.builder()
                    .tinyHouse(h7).tenant(tenant2)
                    .startDate(d4Start).endDate(d4End)
                    .totalDays((int) ChronoUnit.DAYS.between(d4Start, d4End))
                    .totalPrice(new BigDecimal("2475"))
                    .reservationStatus(ReservationStatus.PENDING)
                    .paymentStatus(PaymentStatus.PENDING)
                    .build());

            reviewRepo.save(Review.builder()
                    .tinyHouse(h1).tenant(tenant1)
                    .rating(new BigDecimal("5.0"))
                    .comment("Muhteşem bir deneyimdi! Boğaz manzarası nefes kesici. Kesinlikle tekrar geleceğim.")
                    .build());
            reviewRepo.save(Review.builder()
                    .tinyHouse(h3).tenant(tenant2)
                    .rating(new BigDecimal("5.0"))
                    .comment("Olimpos'un büyüsü ile modern konfor bir arada. Harika bir konaklama!")
                    .build());
            reviewRepo.save(Review.builder()
                    .tinyHouse(h5).tenant(tenant1)
                    .rating(new BigDecimal("4.0"))
                    .comment("Ölüdeniz manzarası muhteşem. Ev temiz ve konforlu. Harika bir deneyimdi.")
                    .build());
            reviewRepo.save(Review.builder()
                    .tinyHouse(h7).tenant(tenant2)
                    .rating(new BigDecimal("5.0"))
                    .comment("Kış tatilinde gittik, şömine başında harika vakit geçirdik. Çok huzurlu bir ortam.")
                    .build());
            reviewRepo.save(Review.builder()
                    .tinyHouse(h10).tenant(tenant1)
                    .rating(new BigDecimal("5.0"))
                    .comment("Sabah balonları izlemek büyüleyiciydi. Ev çok özenli ve temiz. Mutlaka gidin!")
                    .build());

            log.info("=== Database Seeding Complete ===");
            log.info("==========================================");
            log.info("  HAZIR HESAPLAR:");
            log.info("  Admin  : admin@tinyhouse.com / admin123");
            log.info("  Owner  : ahmet@tinyhouse.com / 123456");
            log.info("  Owner  : fatma@tinyhouse.com / 123456");
            log.info("  Tenant : mehmet@tinyhouse.com / 123456");
            log.info("  Tenant : elif@tinyhouse.com / 123456");
            log.info("==========================================");
        };
    }
}
