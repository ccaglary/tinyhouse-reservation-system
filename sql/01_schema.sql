-- ============================================
-- TinyHouse Reservation System
-- DDL Script - PostgreSQL
-- Veritabanı Şema Tanımları
-- ============================================

-- Veritabanı oluşturma (gerekirse)
-- CREATE DATABASE tinyhousedb;

-- UUID desteği
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================
-- 1. USERS TABLOSU
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id                 BIGSERIAL PRIMARY KEY,
    first_name         VARCHAR(50)  NOT NULL,
    last_name          VARCHAR(50)  NOT NULL,
    email              VARCHAR(100) NOT NULL UNIQUE,
    password           VARCHAR(255) NOT NULL,
    phone_number       VARCHAR(20),
    role               VARCHAR(20)  NOT NULL,
    active             BOOLEAN      NOT NULL DEFAULT TRUE,
    profile_image      VARCHAR(255),
    email_verified     BOOLEAN      NOT NULL DEFAULT FALSE,
    refresh_token      VARCHAR(512),
    verification_token VARCHAR(512),
    reset_token        VARCHAR(512),
    created_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP,
    deleted            BOOLEAN      NOT NULL DEFAULT FALSE,
    version            BIGINT       DEFAULT 0,
    CONSTRAINT chk_user_role CHECK (role IN ('ADMIN', 'OWNER', 'TENANT'))
);

CREATE INDEX IF NOT EXISTS idx_user_email ON users (email);
CREATE INDEX IF NOT EXISTS idx_user_role  ON users (role);

-- ============================================
-- 2. TINY_HOUSES TABLOSU
-- ============================================
CREATE TABLE IF NOT EXISTS tiny_houses (
    id              BIGSERIAL      PRIMARY KEY,
    title           VARCHAR(150)   NOT NULL,
    description     TEXT           NOT NULL,
    city            VARCHAR(50)    NOT NULL,
    district        VARCHAR(50),
    address         VARCHAR(255)   NOT NULL,
    latitude        DOUBLE PRECISION,
    longitude       DOUBLE PRECISION,
    nightly_price   NUMERIC(10,2)  NOT NULL,
    cleaning_fee    NUMERIC(10,2)  DEFAULT 0,
    capacity        INTEGER        NOT NULL,
    room_count      INTEGER        NOT NULL DEFAULT 1,
    bed_count       INTEGER        NOT NULL DEFAULT 1,
    bathroom_count  INTEGER        NOT NULL DEFAULT 1,
    wifi            BOOLEAN        DEFAULT FALSE,
    parking         BOOLEAN        DEFAULT FALSE,
    air_conditioner BOOLEAN        DEFAULT FALSE,
    pet_allowed     BOOLEAN        DEFAULT FALSE,
    average_rating  NUMERIC(3,2)   DEFAULT 0,
    active          BOOLEAN        NOT NULL DEFAULT TRUE,
    owner_id        BIGINT         NOT NULL,
    created_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP,
    deleted         BOOLEAN        NOT NULL DEFAULT FALSE,
    version         BIGINT         DEFAULT 0,
    CONSTRAINT fk_th_owner FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_th_city   ON tiny_houses (city);
CREATE INDEX IF NOT EXISTS idx_th_active ON tiny_houses (active);
CREATE INDEX IF NOT EXISTS idx_th_price  ON tiny_houses (nightly_price);
CREATE INDEX IF NOT EXISTS idx_th_owner  ON tiny_houses (owner_id);

-- ============================================
-- 3. TINY_HOUSE_IMAGES TABLOSU
-- ============================================
CREATE TABLE IF NOT EXISTS tiny_house_images (
    id            BIGSERIAL    PRIMARY KEY,
    image_url     VARCHAR(512) NOT NULL,
    cover_image   BOOLEAN      NOT NULL DEFAULT FALSE,
    tiny_house_id BIGINT       NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP,
    deleted       BOOLEAN      NOT NULL DEFAULT FALSE,
    version       BIGINT       DEFAULT 0,
    CONSTRAINT fk_thi_house FOREIGN KEY (tiny_house_id) REFERENCES tiny_houses (id) ON DELETE CASCADE
);

-- ============================================
-- 4. AVAILABILITY_CALENDAR TABLOSU
-- ============================================
CREATE TABLE IF NOT EXISTS availability_calendar (
    id             BIGSERIAL PRIMARY KEY,
    tiny_house_id  BIGINT    NOT NULL,
    available_date DATE      NOT NULL,
    available      BOOLEAN   NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP,
    deleted        BOOLEAN   NOT NULL DEFAULT FALSE,
    version        BIGINT    DEFAULT 0,
    CONSTRAINT fk_ac_house FOREIGN KEY (tiny_house_id) REFERENCES tiny_houses (id) ON DELETE CASCADE,
    CONSTRAINT uq_ac_house_date UNIQUE (tiny_house_id, available_date)
);

-- ============================================
-- 5. RESERVATIONS TABLOSU
-- ============================================
CREATE TABLE IF NOT EXISTS reservations (
    id                  BIGSERIAL     PRIMARY KEY,
    tenant_id           BIGINT        NOT NULL,
    tiny_house_id       BIGINT        NOT NULL,
    start_date          DATE          NOT NULL,
    end_date            DATE          NOT NULL,
    total_days          INTEGER       NOT NULL,
    total_price         NUMERIC(12,2) NOT NULL,
    reservation_status  VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    payment_status      VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    special_request     TEXT,
    created_at          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP,
    deleted             BOOLEAN       NOT NULL DEFAULT FALSE,
    version             BIGINT        DEFAULT 0,
    CONSTRAINT fk_res_tenant FOREIGN KEY (tenant_id)    REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_res_house  FOREIGN KEY (tiny_house_id) REFERENCES tiny_houses (id) ON DELETE CASCADE,
    CONSTRAINT chk_res_status CHECK (reservation_status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED', 'REJECTED')),
    CONSTRAINT chk_res_payment CHECK (payment_status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED')),
    CONSTRAINT chk_res_dates CHECK (end_date > start_date)
);

CREATE INDEX IF NOT EXISTS idx_res_tenant ON reservations (tenant_id);
CREATE INDEX IF NOT EXISTS idx_res_house  ON reservations (tiny_house_id);
CREATE INDEX IF NOT EXISTS idx_res_status ON reservations (reservation_status);

-- ============================================
-- 6. PAYMENTS TABLOSU
-- ============================================
CREATE TABLE IF NOT EXISTS payments (
    id              BIGSERIAL     PRIMARY KEY,
    reservation_id  BIGINT        NOT NULL UNIQUE,
    payment_method  VARCHAR(20)   NOT NULL,
    payment_status  VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    amount          NUMERIC(12,2) NOT NULL,
    transaction_id  VARCHAR(100)  UNIQUE,
    payment_date    TIMESTAMP,
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP,
    deleted         BOOLEAN       NOT NULL DEFAULT FALSE,
    version         BIGINT        DEFAULT 0,
    CONSTRAINT fk_pay_reservation FOREIGN KEY (reservation_id) REFERENCES reservations (id) ON DELETE CASCADE,
    CONSTRAINT chk_pay_method CHECK (payment_method IN ('CREDIT_CARD', 'DEBIT_CARD', 'BANK_TRANSFER', 'PAYPAL')),
    CONSTRAINT chk_pay_status CHECK (payment_status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED'))
);

-- ============================================
-- 7. REVIEWS TABLOSU
-- ============================================
CREATE TABLE IF NOT EXISTS reviews (
    id            BIGSERIAL   PRIMARY KEY,
    rating        NUMERIC     NOT NULL,
    comment       TEXT,
    owner_reply   TEXT,
    tiny_house_id BIGINT      NOT NULL,
    tenant_id     BIGINT      NOT NULL,
    created_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP,
    deleted       BOOLEAN     NOT NULL DEFAULT FALSE,
    version       BIGINT      DEFAULT 0,
    CONSTRAINT fk_rev_house  FOREIGN KEY (tiny_house_id) REFERENCES tiny_houses (id) ON DELETE CASCADE,
    CONSTRAINT fk_rev_tenant FOREIGN KEY (tenant_id)     REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_review_tenant_house UNIQUE (tenant_id, tiny_house_id),
    CONSTRAINT chk_review_rating CHECK (rating >= 0 AND rating <= 5)
);

-- ============================================
-- 8. NOTIFICATIONS TABLOSU
-- ============================================
CREATE TABLE IF NOT EXISTS notifications (
    id                BIGSERIAL    PRIMARY KEY,
    user_id           BIGINT       NOT NULL,
    title             VARCHAR(200) NOT NULL,
    message           TEXT         NOT NULL,
    notification_type VARCHAR(30)  NOT NULL,
    is_read           BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP,
    deleted           BOOLEAN      NOT NULL DEFAULT FALSE,
    version           BIGINT       DEFAULT 0,
    CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT chk_notif_type CHECK (notification_type IN (
        'RESERVATION_CREATED', 'RESERVATION_CONFIRMED', 'RESERVATION_CANCELLED',
        'RESERVATION_REJECTED', 'PAYMENT_COMPLETED', 'PAYMENT_FAILED',
        'REVIEW_RECEIVED', 'SYSTEM_ANNOUNCEMENT'
    ))
);

-- ============================================
-- 9. FAVORITES TABLOSU
-- ============================================
CREATE TABLE IF NOT EXISTS favorites (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT    NOT NULL,
    tiny_house_id BIGINT    NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP,
    deleted       BOOLEAN   NOT NULL DEFAULT FALSE,
    version       BIGINT    DEFAULT 0,
    CONSTRAINT fk_fav_user  FOREIGN KEY (user_id)       REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_fav_house FOREIGN KEY (tiny_house_id) REFERENCES tiny_houses (id) ON DELETE CASCADE,
    CONSTRAINT uq_fav_user_house UNIQUE (user_id, tiny_house_id)
);

-- ============================================
-- 10. STORED PROCEDURES (İş Mantığı)
-- ============================================

-- Rezervasyon Oluşturma Prosedürü
-- Fiyat hesaplama, tarih çakışma kontrolü ve INSERT işlemini tek seferde yapar
CREATE OR REPLACE FUNCTION sp_create_reservation(
    p_tenant_id BIGINT,
    p_tiny_house_id BIGINT,
    p_start_date DATE,
    p_end_date DATE,
    p_special_request TEXT DEFAULT NULL
) RETURNS BIGINT AS $$
DECLARE
    v_nightly_price NUMERIC(10,2);
    v_cleaning_fee NUMERIC(10,2);
    v_total_days INT;
    v_total_price NUMERIC(12,2);
    v_conflict_count INT;
    v_reservation_id BIGINT;
BEGIN
    SELECT nightly_price, COALESCE(cleaning_fee, 0)
    INTO v_nightly_price, v_cleaning_fee
    FROM tiny_houses WHERE id = p_tiny_house_id AND deleted = false AND active = true;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Tiny House bulunamadı veya aktif değil';
    END IF;

    SELECT COUNT(*) INTO v_conflict_count
    FROM reservations
    WHERE tiny_house_id = p_tiny_house_id
      AND deleted = false
      AND reservation_status NOT IN ('CANCELLED', 'REJECTED')
      AND start_date <= p_end_date
      AND end_date >= p_start_date;

    IF v_conflict_count > 0 THEN
        RAISE EXCEPTION 'Seçilen tarihler için çakışan rezervasyon bulunmaktadır';
    END IF;

    v_total_days := p_end_date - p_start_date;
    v_total_price := (v_nightly_price * v_total_days) + v_cleaning_fee;

    INSERT INTO reservations (tenant_id, tiny_house_id, start_date, end_date, total_days, total_price,
                              reservation_status, payment_status, special_request, created_at, updated_at, deleted, version)
    VALUES (p_tenant_id, p_tiny_house_id, p_start_date, p_end_date, v_total_days, v_total_price,
            'PENDING', 'PENDING', p_special_request, NOW(), NOW(), false, 0)
    RETURNING id INTO v_reservation_id;

    RETURN v_reservation_id;
END;
$$ LANGUAGE plpgsql;

-- Ödeme Tamamlama Prosedürü
-- Onaylanmış rezervasyona ödeme kaydı oluşturur ve durumu günceller
CREATE OR REPLACE FUNCTION sp_complete_payment(
    p_reservation_id BIGINT,
    p_payment_method VARCHAR(20)
) RETURNS VARCHAR AS $$
DECLARE
    v_amount NUMERIC(12,2);
    v_status VARCHAR(20);
    v_transaction_id VARCHAR(100);
BEGIN
    SELECT total_price, reservation_status INTO v_amount, v_status
    FROM reservations WHERE id = p_reservation_id AND deleted = false;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Rezervasyon bulunamadı';
    END IF;

    IF v_status != 'CONFIRMED' THEN
        RAISE EXCEPTION 'Yalnızca onaylanmış rezervasyonlar için ödeme yapılabilir';
    END IF;

    v_transaction_id := 'TXN-' || UPPER(SUBSTRING(uuid_generate_v4()::text, 1, 8));

    INSERT INTO payments (reservation_id, payment_method, payment_status, amount, transaction_id,
                          payment_date, created_at, updated_at, deleted, version)
    VALUES (p_reservation_id, p_payment_method, 'COMPLETED', v_amount, v_transaction_id,
            NOW(), NOW(), NOW(), false, 0);

    UPDATE reservations SET payment_status = 'COMPLETED', updated_at = NOW()
    WHERE id = p_reservation_id;

    RETURN v_transaction_id;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- 11. FUNCTIONS (Yardımcı Fonksiyonlar)
-- ============================================

-- Ortalama Puan Hesaplama
-- Bir tiny house'un tüm değerlendirmelerinin ortalamasını döndürür
CREATE OR REPLACE FUNCTION fn_calculate_average_rating(p_house_id BIGINT)
RETURNS NUMERIC(3,2) AS $$
DECLARE
    v_avg NUMERIC(3,2);
BEGIN
    SELECT AVG(rating) INTO v_avg
    FROM reviews
    WHERE tiny_house_id = p_house_id AND deleted = false;

    RETURN COALESCE(v_avg, 0.00);
END;
$$ LANGUAGE plpgsql;

-- Toplam Gelir Hesaplama
-- Belirli bir ev sahibinin veya tüm platformun toplam gelirini döndürür
CREATE OR REPLACE FUNCTION fn_calculate_total_revenue(p_owner_id BIGINT DEFAULT NULL)
RETURNS NUMERIC(15,2) AS $$
DECLARE
    v_total NUMERIC(15,2);
BEGIN
    IF p_owner_id IS NOT NULL THEN
        SELECT COALESCE(SUM(r.total_price), 0) INTO v_total
        FROM reservations r
        JOIN tiny_houses th ON r.tiny_house_id = th.id
        WHERE th.owner_id = p_owner_id
          AND r.reservation_status = 'COMPLETED'
          AND r.deleted = false;
    ELSE
        SELECT COALESCE(SUM(total_price), 0) INTO v_total
        FROM reservations
        WHERE reservation_status = 'COMPLETED' AND deleted = false;
    END IF;

    RETURN v_total;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- 12. TRIGGERS (Tetikleyiciler)
-- ============================================

-- Değerlendirme Sonrası Otomatik Puan Güncelleme
-- Bir yorum eklendiğinde/güncellendiğinde/silindiğinde ilgili evin
-- average_rating alanını otomatik olarak yeniden hesaplar
CREATE OR REPLACE FUNCTION trg_update_average_rating()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE tiny_houses
    SET average_rating = fn_calculate_average_rating(
        CASE WHEN TG_OP = 'DELETE' THEN OLD.tiny_house_id ELSE NEW.tiny_house_id END
    ),
    updated_at = NOW()
    WHERE id = CASE WHEN TG_OP = 'DELETE' THEN OLD.tiny_house_id ELSE NEW.tiny_house_id END;

    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_review_rating_update ON reviews;
CREATE TRIGGER trg_review_rating_update
    AFTER INSERT OR UPDATE OR DELETE ON reviews
    FOR EACH ROW
    EXECUTE FUNCTION trg_update_average_rating();

-- Rezervasyon Tarih Çakışma Kontrolü
-- Aynı tiny house için aynı tarihlerde birden fazla aktif
-- rezervasyon oluşturulmasını veritabanı seviyesinde engeller
CREATE OR REPLACE FUNCTION trg_check_reservation_overlap()
RETURNS TRIGGER AS $$
DECLARE
    v_conflict_count INT;
BEGIN
    IF NEW.reservation_status IN ('CANCELLED', 'REJECTED') THEN
        RETURN NEW;
    END IF;

    SELECT COUNT(*) INTO v_conflict_count
    FROM reservations
    WHERE id != NEW.id
      AND tiny_house_id = NEW.tiny_house_id
      AND deleted = false
      AND reservation_status NOT IN ('CANCELLED', 'REJECTED')
      AND start_date <= NEW.end_date
      AND end_date >= NEW.start_date;

    IF v_conflict_count > 0 THEN
        RAISE EXCEPTION 'Bu tarih aralığında çakışan bir rezervasyon bulunmaktadır';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_reservation_overlap_check ON reservations;
CREATE TRIGGER trg_reservation_overlap_check
    BEFORE INSERT OR UPDATE ON reservations
    FOR EACH ROW
    EXECUTE FUNCTION trg_check_reservation_overlap();

-- ============================================
-- 13. VARSAYILAN ADMIN KULLANICISI
-- ============================================
-- Şifre: admin123 (BCrypt hash)
INSERT INTO users (first_name, last_name, email, password, role, active, email_verified,
                   created_at, updated_at, deleted, version)
VALUES ('Admin', 'TinyHouse', 'admin@tinyhouse.com',
        '$2a$10$rDkPvvAFV8kqwvKJzwlRj.S0sOoXAPz0CVlhISKy0igImOncXbEBK',
        'ADMIN', true, true, NOW(), NOW(), false, 0)
ON CONFLICT (email) DO NOTHING;
