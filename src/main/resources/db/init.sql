

-- TinyHouse Database Initialization Script

-- Create extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


-- STORED PROCEDURES


-- 1. Reservation Create Process
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
    -- Get pricing
    SELECT nightly_price, COALESCE(cleaning_fee, 0)
    INTO v_nightly_price, v_cleaning_fee
    FROM tiny_houses WHERE id = p_tiny_house_id AND deleted = false AND active = true;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Tiny House bulunamadı veya aktif değil';
    END IF;

    -- Check date conflicts
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

    -- Calculate pricing
    v_total_days := p_end_date - p_start_date;
    v_total_price := (v_nightly_price * v_total_days) + v_cleaning_fee;

    -- Insert reservation
    INSERT INTO reservations (tenant_id, tiny_house_id, start_date, end_date, total_days, total_price,
                              reservation_status, payment_status, special_request, created_at, updated_at, deleted, version)
    VALUES (p_tenant_id, p_tiny_house_id, p_start_date, p_end_date, v_total_days, v_total_price,
            'PENDING', 'PENDING', p_special_request, NOW(), NOW(), false, 0)
    RETURNING id INTO v_reservation_id;

    RETURN v_reservation_id;
END;
$$ LANGUAGE plpgsql;

-- 2. Payment Completion Process
CREATE OR REPLACE FUNCTION sp_complete_payment(
    p_reservation_id BIGINT,
    p_payment_method VARCHAR(20)
) RETURNS VARCHAR AS $$
DECLARE
    v_amount NUMERIC(12,2);
    v_status VARCHAR(20);
    v_transaction_id VARCHAR(100);
BEGIN
    -- Get reservation
    SELECT total_price, reservation_status INTO v_amount, v_status
    FROM reservations WHERE id = p_reservation_id AND deleted = false;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Rezervasyon bulunamadı';
    END IF;

    IF v_status != 'CONFIRMED' THEN
        RAISE EXCEPTION 'Yalnızca onaylanmış rezervasyonlar için ödeme yapılabilir';
    END IF;

    -- Generate transaction ID
    v_transaction_id := 'TXN-' || UPPER(SUBSTRING(uuid_generate_v4()::text, 1, 8));

    -- Create payment
    INSERT INTO payments (reservation_id, payment_method, payment_status, amount, transaction_id,
                          payment_date, created_at, updated_at, deleted, version)
    VALUES (p_reservation_id, p_payment_method, 'COMPLETED', v_amount, v_transaction_id,
            NOW(), NOW(), NOW(), false, 0);

    -- Update reservation
    UPDATE reservations SET payment_status = 'COMPLETED', updated_at = NOW()
    WHERE id = p_reservation_id;

    RETURN v_transaction_id;
END;
$$ LANGUAGE plpgsql;




-- FUNCTIONS


-- 1. Calculate Average Rating
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

-- 2. Calculate Total Revenue
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

-- TRIGGERS

-- 1. Update average rating when review is added/modified
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

-- 2. Prevent overlapping reservations
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


INSERT INTO users (first_name, last_name, email, password, role, active, email_verified, created_at, updated_at, deleted, version)
VALUES ('Admin', 'TinyHouse', 'admin@tinyhouse.com',
        '$2a$10$rDkPvvAFV8kqwvKJzwlRj.S0sOoXAPz0CVlhISKy0igImOncXbEBK',
        'ADMIN', true, true, NOW(), NOW(), false, 0)
ON CONFLICT (email) DO NOTHING;
