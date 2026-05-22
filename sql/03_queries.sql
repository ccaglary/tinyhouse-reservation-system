-- ============================================
-- TinyHouse Reservation System
-- Faydalı Sorgular (Utility Queries)
-- ============================================

-- ============================================
-- RAPORLAMA SORGULARI
-- ============================================

-- Tüm kullanıcıları listele
SELECT id, first_name, last_name, email, role, active, email_verified, created_at
FROM users
WHERE deleted = FALSE
ORDER BY created_at DESC;

-- Şehre göre tiny house sayısı
SELECT city, COUNT(*) AS house_count, AVG(nightly_price) AS avg_price
FROM tiny_houses
WHERE deleted = FALSE AND active = TRUE
GROUP BY city
ORDER BY house_count DESC;

-- En yüksek puanlı evler (Top 10)
SELECT th.id, th.title, th.city, th.nightly_price, th.average_rating,
       u.first_name || ' ' || u.last_name AS owner_name
FROM tiny_houses th
JOIN users u ON th.owner_id = u.id
WHERE th.deleted = FALSE AND th.active = TRUE
ORDER BY th.average_rating DESC
LIMIT 10;

-- Aktif rezervasyonlar
SELECT r.id, r.start_date, r.end_date, r.total_price, r.reservation_status,
       th.title AS house_title,
       u.first_name || ' ' || u.last_name AS tenant_name
FROM reservations r
JOIN tiny_houses th ON r.tiny_house_id = th.id
JOIN users u ON r.tenant_id = u.id
WHERE r.deleted = FALSE
  AND r.reservation_status IN ('PENDING', 'CONFIRMED')
ORDER BY r.start_date;

-- Ev sahibine göre gelir raporu
SELECT u.first_name || ' ' || u.last_name AS owner_name,
       COUNT(DISTINCT th.id) AS house_count,
       COUNT(r.id) AS reservation_count,
       COALESCE(SUM(r.total_price), 0) AS total_revenue
FROM users u
JOIN tiny_houses th ON u.id = th.owner_id
LEFT JOIN reservations r ON th.id = r.tiny_house_id AND r.deleted = FALSE AND r.reservation_status = 'COMPLETED'
WHERE u.role = 'OWNER' AND u.deleted = FALSE
GROUP BY u.id, u.first_name, u.last_name
ORDER BY total_revenue DESC;

-- Aylık rezervasyon istatistikleri
SELECT DATE_TRUNC('month', r.created_at) AS month,
       COUNT(*) AS reservation_count,
       SUM(r.total_price) AS total_revenue,
       AVG(r.total_days) AS avg_stay_days
FROM reservations r
WHERE r.deleted = FALSE
GROUP BY DATE_TRUNC('month', r.created_at)
ORDER BY month DESC;

-- Okunmamış bildirimler
SELECT n.id, n.title, n.message, n.notification_type, n.created_at,
       u.first_name || ' ' || u.last_name AS user_name
FROM notifications n
JOIN users u ON n.user_id = u.id
WHERE n.is_read = FALSE AND n.deleted = FALSE
ORDER BY n.created_at DESC;

-- Favori istatistikleri
SELECT th.title, th.city, COUNT(f.id) AS favorite_count
FROM favorites f
JOIN tiny_houses th ON f.tiny_house_id = th.id
WHERE f.deleted = FALSE AND th.deleted = FALSE
GROUP BY th.id, th.title, th.city
ORDER BY favorite_count DESC;

-- Belirli tarih aralığında müsait evler
SELECT th.id, th.title, th.city, th.nightly_price, th.capacity
FROM tiny_houses th
WHERE th.deleted = FALSE AND th.active = TRUE
  AND th.id NOT IN (
      SELECT r.tiny_house_id
      FROM reservations r
      WHERE r.deleted = FALSE
        AND r.reservation_status IN ('PENDING', 'CONFIRMED')
        AND r.start_date < '2026-07-01'
        AND r.end_date > '2026-06-25'
  )
ORDER BY th.nightly_price;

-- Ödeme durumu raporu
SELECT r.id AS reservation_id,
       th.title AS house_title,
       r.total_price,
       r.reservation_status,
       r.payment_status,
       p.payment_method,
       p.transaction_id,
       p.payment_date
FROM reservations r
JOIN tiny_houses th ON r.tiny_house_id = th.id
LEFT JOIN payments p ON r.id = p.reservation_id
WHERE r.deleted = FALSE
ORDER BY r.created_at DESC;

-- Değerlendirme ortalamaları (ev bazlı)
SELECT th.title, th.city,
       COUNT(rv.id) AS review_count,
       AVG(rv.rating) AS avg_rating,
       MIN(rv.rating) AS min_rating,
       MAX(rv.rating) AS max_rating
FROM tiny_houses th
LEFT JOIN reviews rv ON th.id = rv.tiny_house_id AND rv.deleted = FALSE
WHERE th.deleted = FALSE
GROUP BY th.id, th.title, th.city
HAVING COUNT(rv.id) > 0
ORDER BY avg_rating DESC;

-- ============================================
-- BAKIM SORGULARI
-- ============================================

-- Soft-deleted kayıtları temizle (30 günden eski)
DELETE FROM notifications WHERE deleted = TRUE AND updated_at < CURRENT_TIMESTAMP - INTERVAL '30 days';
DELETE FROM favorites WHERE deleted = TRUE AND updated_at < CURRENT_TIMESTAMP - INTERVAL '30 days';
DELETE FROM reviews WHERE deleted = TRUE AND updated_at < CURRENT_TIMESTAMP - INTERVAL '30 days';
DELETE FROM payments WHERE deleted = TRUE AND updated_at < CURRENT_TIMESTAMP - INTERVAL '30 days';
DELETE FROM reservations WHERE deleted = TRUE AND updated_at < CURRENT_TIMESTAMP - INTERVAL '30 days';
DELETE FROM tiny_house_images WHERE deleted = TRUE AND updated_at < CURRENT_TIMESTAMP - INTERVAL '30 days';
DELETE FROM availability_calendar WHERE deleted = TRUE AND updated_at < CURRENT_TIMESTAMP - INTERVAL '30 days';
DELETE FROM tiny_houses WHERE deleted = TRUE AND updated_at < CURRENT_TIMESTAMP - INTERVAL '30 days';

-- Tablo boyutları ve kayıt sayıları
SELECT
    relname AS table_name,
    n_live_tup AS row_count,
    pg_size_pretty(pg_total_relation_size(relid)) AS total_size
FROM pg_stat_user_tables
ORDER BY n_live_tup DESC;

-- Index kullanım istatistikleri
SELECT
    schemaname,
    relname AS table_name,
    indexrelname AS index_name,
    idx_scan AS times_used,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size
FROM pg_stat_user_indexes
ORDER BY idx_scan DESC;
