-- ============================================
-- TinyHouse Reservation System
-- DML Script - Seed Data
-- Örnek Veri Ekleme
-- ============================================
-- Not: Şifreler BCrypt ile hash'lenmiştir.
--   admin123 -> $2a$10$...
--   123456   -> $2a$10$...
-- Bu script'i çalıştırmadan önce uygulamayı bir kez başlatarak
-- DataSeeder ile verilerin oluşturulmasını sağlayabilir veya
-- aşağıdaki INSERT'leri doğrudan çalıştırabilirsiniz.
-- ============================================

-- ============================================
-- 1. KULLANICILAR
-- ============================================
-- Not: password hash'leri BCrypt formatındadır. Uygulama üzerinden
-- kayıt yapmanız önerilir. Aşağıdaki hash'ler örnek amaçlıdır.

INSERT INTO users (first_name, last_name, email, password, phone_number, role, active, email_verified, created_at, deleted, version)
VALUES
    ('Admin', 'TinyHouse', 'admin@tinyhouse.com',
     '$2a$10$dXJ3SW6G7P50lGmMQgel2uaRV1JTOmZQWPB0l4v8FLRd1DBRSDKVC',
     '05551234567', 'ADMIN', TRUE, TRUE, CURRENT_TIMESTAMP, FALSE, 0),

    ('Ahmet', 'Yılmaz', 'ahmet@tinyhouse.com',
     '$2a$10$8KzMwWE1FgVJlT8g0g5QxO4TXhPDw6fK9g5K5g5K5g5K5g5K5g5K5',
     '05359876543', 'OWNER', TRUE, TRUE, CURRENT_TIMESTAMP, FALSE, 0),

    ('Fatma', 'Demir', 'fatma@tinyhouse.com',
     '$2a$10$8KzMwWE1FgVJlT8g0g5QxO4TXhPDw6fK9g5K5g5K5g5K5g5K5g5K5',
     '05423456789', 'OWNER', TRUE, TRUE, CURRENT_TIMESTAMP, FALSE, 0),

    ('Mehmet', 'Kaya', 'mehmet@tinyhouse.com',
     '$2a$10$8KzMwWE1FgVJlT8g0g5QxO4TXhPDw6fK9g5K5g5K5g5K5g5K5g5K5',
     '05301112233', 'TENANT', TRUE, TRUE, CURRENT_TIMESTAMP, FALSE, 0),

    ('Elif', 'Çelik', 'elif@tinyhouse.com',
     '$2a$10$8KzMwWE1FgVJlT8g0g5QxO4TXhPDw6fK9g5K5g5K5g5K5g5K5g5K5',
     '05444455566', 'TENANT', TRUE, TRUE, CURRENT_TIMESTAMP, FALSE, 0);

-- ============================================
-- 2. TINY HOUSE'LAR
-- ============================================
INSERT INTO tiny_houses (title, description, city, district, address, latitude, longitude,
                         nightly_price, cleaning_fee, capacity, room_count, bed_count, bathroom_count,
                         wifi, parking, air_conditioner, pet_allowed, average_rating, active,
                         owner_id, created_at, deleted, version)
VALUES
    ('Boğaz Manzaralı Modern Tiny House',
     'İstanbul Boğazı''nın eşsiz manzarasına sahip, modern tasarımlı tiny house. Tam donanımlı mutfak, rahat yatak ve geniş teras ile unutulmaz bir konaklama deneyimi sunar.',
     'İstanbul', 'Beykoz', 'Beykoz Korusu Yolu No:15', 41.1325, 29.0965,
     850.00, 150.00, 4, 2, 2, 1, TRUE, TRUE, TRUE, FALSE, 4.80, TRUE,
     (SELECT id FROM users WHERE email = 'ahmet@tinyhouse.com'), CURRENT_TIMESTAMP, FALSE, 0),

    ('Adalar Manzaralı Sahil Evi',
     'Prens Adaları manzarasına bakan, sahile sıfır konumda romantik tiny house. Deniz sesi eşliğinde huzurlu bir tatil için ideal.',
     'İstanbul', 'Maltepe', 'Sahil Yolu Cad. No:42', 40.9332, 29.1355,
     720.00, 100.00, 2, 1, 1, 1, TRUE, FALSE, TRUE, TRUE, 4.60, TRUE,
     (SELECT id FROM users WHERE email = 'ahmet@tinyhouse.com'), CURRENT_TIMESTAMP, FALSE, 0),

    ('Olimpos Orman Evi',
     'Olimpos''un eşsiz doğasında, çam ormanları arasında konumlanmış rustic tarzda tiny house. Antik kent yürüyüş mesafesinde.',
     'Antalya', 'Kumluca', 'Olimpos Yolu Km:3', 36.3957, 30.4728,
     550.00, 75.00, 3, 1, 2, 1, TRUE, TRUE, FALSE, TRUE, 4.90, TRUE,
     (SELECT id FROM users WHERE email = 'fatma@tinyhouse.com'), CURRENT_TIMESTAMP, FALSE, 0),

    ('Kaş Deniz Manzaralı Villa Tiny',
     'Kaş''ın turkuaz sularına bakan, özel havuzlu lüks tiny house. Akdeniz mimarisinden esinlenilmiş tasarımıyla göz kamaştırıyor.',
     'Antalya', 'Kaş', 'Çukurbağ Yarımadası No:8', 36.2001, 29.6389,
     1200.00, 200.00, 4, 2, 2, 2, TRUE, TRUE, TRUE, FALSE, 4.70, TRUE,
     (SELECT id FROM users WHERE email = 'fatma@tinyhouse.com'), CURRENT_TIMESTAMP, FALSE, 0),

    ('Fethiye Ölüdeniz Dağ Evi',
     'Ölüdeniz''in ünlü lagününe tepeden bakan, panoramik manzaralı dağ evi. Yamaç paraşütü pistine yakın konumda.',
     'Muğla', 'Fethiye', 'Babadağ Eteği Köyiçi No:23', 36.5501, 29.1167,
     680.00, 100.00, 2, 1, 1, 1, TRUE, TRUE, TRUE, FALSE, 4.85, TRUE,
     (SELECT id FROM users WHERE email = 'ahmet@tinyhouse.com'), CURRENT_TIMESTAMP, FALSE, 0),

    ('Bodrum Taş Ev Tiny House',
     'Bodrum''un tarihi dokusuna uygun taş mimarisiyle inşa edilmiş butik tiny house. Gümüşlük sahiline yürüme mesafesinde.',
     'Muğla', 'Bodrum', 'Gümüşlük Mah. Sahil Sk. No:7', 37.0519, 27.2281,
     950.00, 150.00, 3, 1, 2, 1, TRUE, FALSE, TRUE, TRUE, 4.50, TRUE,
     (SELECT id FROM users WHERE email = 'fatma@tinyhouse.com'), CURRENT_TIMESTAMP, FALSE, 0),

    ('Abant Gölü Orman Kaçamağı',
     'Abant Gölü manzaralı, kış-yaz her mevsim keyifli bir kaçamak noktası. Şömineli oturma odası ve geniş balkon.',
     'Bolu', 'Mudurnu', 'Abant Tabiat Parkı Girişi', 40.6013, 31.2735,
     480.00, 75.00, 4, 2, 2, 1, TRUE, TRUE, FALSE, TRUE, 4.75, TRUE,
     (SELECT id FROM users WHERE email = 'ahmet@tinyhouse.com'), CURRENT_TIMESTAMP, FALSE, 0),

    ('Alaçatı Rüzgâr Evi',
     'Alaçatı''nın renkli sokaklarında, yel değirmenlerine yakın konumda şirin bir tiny house. Sörf için mükemmel rüzgâr koşulları.',
     'İzmir', 'Çeşme', 'Alaçatı Mahallesi 3001 Sk. No:12', 38.2783, 26.3731,
     780.00, 100.00, 2, 1, 1, 1, TRUE, TRUE, TRUE, FALSE, 4.65, TRUE,
     (SELECT id FROM users WHERE email = 'fatma@tinyhouse.com'), CURRENT_TIMESTAMP, FALSE, 0),

    ('Uzungöl Yayla Evi',
     'Uzungöl''ün büyüleyici manzarasına sahip, geleneksel Karadeniz mimarisinde ahşap tiny house. Çay bahçeleri arasında huzur.',
     'Trabzon', 'Çaykara', 'Uzungöl Mah. Göl Kenarı', 40.6118, 40.2900,
     420.00, 50.00, 3, 1, 2, 1, TRUE, TRUE, FALSE, TRUE, 4.55, TRUE,
     (SELECT id FROM users WHERE email = 'ahmet@tinyhouse.com'), CURRENT_TIMESTAMP, FALSE, 0),

    ('Kapadokya Peribacası Tiny House',
     'Göreme''nin eşsiz peri bacalarına bakan, kaya oyma tasarımlı modern tiny house. Sabah balon seyri için mükemmel konum.',
     'Nevşehir', 'Göreme', 'Göreme Kasabası Müze Cad. No:5', 38.6431, 34.8289,
     900.00, 125.00, 2, 1, 1, 1, TRUE, TRUE, TRUE, FALSE, 4.95, TRUE,
     (SELECT id FROM users WHERE email = 'fatma@tinyhouse.com'), CURRENT_TIMESTAMP, FALSE, 0),

    ('Sapanca Göl Kenarı Tiny House',
     'Sapanca Gölü''nün sakin sularına bakan, yeşillikler içinde huzurlu tiny house. Barbekü alanı ve göl manzaralı veranda.',
     'Sakarya', 'Sapanca', 'Göl Kenarı Mevkii No:18', 40.6919, 30.2711,
     520.00, 80.00, 4, 2, 2, 1, TRUE, TRUE, FALSE, TRUE, 4.40, TRUE,
     (SELECT id FROM users WHERE email = 'ahmet@tinyhouse.com'), CURRENT_TIMESTAMP, FALSE, 0),

    ('Akçakoca Sahil Tiny House',
     'Akçakoca''nın gizli koylarından birine bakan, deniz kokulu tiny house. Fındık bahçeleri arasında doğa ile baş başa kalın.',
     'Düzce', 'Akçakoca', 'Sahil Mah. Koy Yolu No:3', 41.0877, 31.1145,
     380.00, 60.00, 2, 1, 1, 1, FALSE, TRUE, FALSE, TRUE, 4.30, TRUE,
     (SELECT id FROM users WHERE email = 'fatma@tinyhouse.com'), CURRENT_TIMESTAMP, FALSE, 0);

-- ============================================
-- 3. TINY HOUSE GÖRSELLERİ
-- ============================================
INSERT INTO tiny_house_images (image_url, cover_image, tiny_house_id, created_at, deleted, version)
VALUES
    ('https://images.unsplash.com/photo-1587061949409-02df41d5e562?w=800&h=500&fit=crop',   TRUE, (SELECT id FROM tiny_houses WHERE title = 'Boğaz Manzaralı Modern Tiny House'),      CURRENT_TIMESTAMP, FALSE, 0),
    ('https://images.unsplash.com/photo-1588880331179-bc9b93a8cb5e?w=800&h=500&fit=crop',   TRUE, (SELECT id FROM tiny_houses WHERE title = 'Adalar Manzaralı Sahil Evi'),               CURRENT_TIMESTAMP, FALSE, 0),
    ('https://images.unsplash.com/photo-1510798831971-661eb04b3739?w=800&h=500&fit=crop',   TRUE, (SELECT id FROM tiny_houses WHERE title = 'Olimpos Orman Evi'),                        CURRENT_TIMESTAMP, FALSE, 0),
    ('https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?w=800&h=500&fit=crop',   TRUE, (SELECT id FROM tiny_houses WHERE title = 'Kaş Deniz Manzaralı Villa Tiny'),           CURRENT_TIMESTAMP, FALSE, 0),
    ('https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=800&h=500&fit=crop',   TRUE, (SELECT id FROM tiny_houses WHERE title = 'Fethiye Ölüdeniz Dağ Evi'),                 CURRENT_TIMESTAMP, FALSE, 0),
    ('https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=800&h=500&fit=crop',   TRUE, (SELECT id FROM tiny_houses WHERE title = 'Bodrum Taş Ev Tiny House'),                 CURRENT_TIMESTAMP, FALSE, 0),
    ('https://images.unsplash.com/photo-1542718610-a1d656d1884c?w=800&h=500&fit=crop',      TRUE, (SELECT id FROM tiny_houses WHERE title = 'Abant Gölü Orman Kaçamağı'),                CURRENT_TIMESTAMP, FALSE, 0),
    ('https://images.unsplash.com/photo-1523217582562-09d0def993a6?w=800&h=500&fit=crop',   TRUE, (SELECT id FROM tiny_houses WHERE title = 'Alaçatı Rüzgâr Evi'),                      CURRENT_TIMESTAMP, FALSE, 0),
    ('https://images.unsplash.com/photo-1540518614846-7eded433c457?w=800&h=500&fit=crop',   TRUE, (SELECT id FROM tiny_houses WHERE title = 'Uzungöl Yayla Evi'),                        CURRENT_TIMESTAMP, FALSE, 0),
    ('https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800&h=500&fit=crop',   TRUE, (SELECT id FROM tiny_houses WHERE title = 'Kapadokya Peribacası Tiny House'),           CURRENT_TIMESTAMP, FALSE, 0),
    ('https://images.unsplash.com/photo-1480074568708-e7b720bb3f09?w=800&h=500&fit=crop',   TRUE, (SELECT id FROM tiny_houses WHERE title = 'Sapanca Göl Kenarı Tiny House'),            CURRENT_TIMESTAMP, FALSE, 0),
    ('https://images.unsplash.com/photo-1449824913935-59a10b8d2000?w=800&h=500&fit=crop',   TRUE, (SELECT id FROM tiny_houses WHERE title = 'Akçakoca Sahil Tiny House'),                CURRENT_TIMESTAMP, FALSE, 0);

-- ============================================
-- 4. REZERVASYONLAR
-- ============================================
INSERT INTO reservations (tenant_id, tiny_house_id, start_date, end_date, total_days, total_price,
                          reservation_status, payment_status, created_at, deleted, version)
VALUES
    ((SELECT id FROM users WHERE email = 'mehmet@tinyhouse.com'),
     (SELECT id FROM tiny_houses WHERE title = 'Boğaz Manzaralı Modern Tiny House'),
     CURRENT_DATE + INTERVAL '5 days', CURRENT_DATE + INTERVAL '8 days', 3, 2700.00,
     'CONFIRMED', 'COMPLETED', CURRENT_TIMESTAMP, FALSE, 0),

    ((SELECT id FROM users WHERE email = 'elif@tinyhouse.com'),
     (SELECT id FROM tiny_houses WHERE title = 'Olimpos Orman Evi'),
     CURRENT_DATE + INTERVAL '10 days', CURRENT_DATE + INTERVAL '14 days', 4, 2275.00,
     'PENDING', 'PENDING', CURRENT_TIMESTAMP, FALSE, 0),

    ((SELECT id FROM users WHERE email = 'mehmet@tinyhouse.com'),
     (SELECT id FROM tiny_houses WHERE title = 'Fethiye Ölüdeniz Dağ Evi'),
     CURRENT_DATE - INTERVAL '10 days', CURRENT_DATE - INTERVAL '7 days', 3, 2140.00,
     'COMPLETED', 'COMPLETED', CURRENT_TIMESTAMP, FALSE, 0),

    ((SELECT id FROM users WHERE email = 'elif@tinyhouse.com'),
     (SELECT id FROM tiny_houses WHERE title = 'Abant Gölü Orman Kaçamağı'),
     CURRENT_DATE + INTERVAL '20 days', CURRENT_DATE + INTERVAL '25 days', 5, 2475.00,
     'PENDING', 'PENDING', CURRENT_TIMESTAMP, FALSE, 0);

-- ============================================
-- 5. DEĞERLENDİRMELER (REVIEWS)
-- ============================================
INSERT INTO reviews (rating, comment, tiny_house_id, tenant_id, created_at, deleted, version)
VALUES
    (5.0, 'Muhteşem bir deneyimdi! Boğaz manzarası nefes kesici. Kesinlikle tekrar geleceğim.',
     (SELECT id FROM tiny_houses WHERE title = 'Boğaz Manzaralı Modern Tiny House'),
     (SELECT id FROM users WHERE email = 'mehmet@tinyhouse.com'),
     CURRENT_TIMESTAMP, FALSE, 0),

    (5.0, 'Olimpos''un büyüsü ile modern konfor bir arada. Harika bir konaklama!',
     (SELECT id FROM tiny_houses WHERE title = 'Olimpos Orman Evi'),
     (SELECT id FROM users WHERE email = 'elif@tinyhouse.com'),
     CURRENT_TIMESTAMP, FALSE, 0),

    (4.0, 'Ölüdeniz manzarası muhteşem. Ev temiz ve konforlu. Harika bir deneyimdi.',
     (SELECT id FROM tiny_houses WHERE title = 'Fethiye Ölüdeniz Dağ Evi'),
     (SELECT id FROM users WHERE email = 'mehmet@tinyhouse.com'),
     CURRENT_TIMESTAMP, FALSE, 0),

    (5.0, 'Kış tatilinde gittik, şömine başında harika vakit geçirdik. Çok huzurlu bir ortam.',
     (SELECT id FROM tiny_houses WHERE title = 'Abant Gölü Orman Kaçamağı'),
     (SELECT id FROM users WHERE email = 'elif@tinyhouse.com'),
     CURRENT_TIMESTAMP, FALSE, 0),

    (5.0, 'Sabah balonları izlemek büyüleyiciydi. Ev çok özenli ve temiz. Mutlaka gidin!',
     (SELECT id FROM tiny_houses WHERE title = 'Kapadokya Peribacası Tiny House'),
     (SELECT id FROM users WHERE email = 'mehmet@tinyhouse.com'),
     CURRENT_TIMESTAMP, FALSE, 0);
