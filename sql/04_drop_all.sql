-- ============================================
-- TinyHouse Reservation System
-- Tüm Tabloları Silme (Drop Script)
-- DİKKAT: Bu script tüm verileri siler!
-- ============================================

-- Bağımlılık sırasına göre silme (child -> parent)
DROP TABLE IF EXISTS favorites CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS payments CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS reservations CASCADE;
DROP TABLE IF EXISTS availability_calendar CASCADE;
DROP TABLE IF EXISTS tiny_house_images CASCADE;
DROP TABLE IF EXISTS tiny_houses CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Sequence'ları temizle (varsa)
DROP SEQUENCE IF EXISTS users_id_seq CASCADE;
DROP SEQUENCE IF EXISTS tiny_houses_id_seq CASCADE;
DROP SEQUENCE IF EXISTS tiny_house_images_id_seq CASCADE;
DROP SEQUENCE IF EXISTS availability_calendar_id_seq CASCADE;
DROP SEQUENCE IF EXISTS reservations_id_seq CASCADE;
DROP SEQUENCE IF EXISTS payments_id_seq CASCADE;
DROP SEQUENCE IF EXISTS reviews_id_seq CASCADE;
DROP SEQUENCE IF EXISTS notifications_id_seq CASCADE;
DROP SEQUENCE IF EXISTS favorites_id_seq CASCADE;
