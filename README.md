# 🏡 Tiny House Rezervasyon ve Yönetim Sistemi

Tiny House konseptindeki evlerin kiralanabileceği, rezervasyon ve yönetim süreçlerini dijitalleştiren full-stack bir web uygulaması.

---

## 🚀 Özellikler

### 👤 Kullanıcı Rolleri
| Rol | Yetkiler |
|-----|----------|
| **Admin** | Tüm kullanıcıları, ilanları ve rezervasyonları yönetir. Mali raporları inceler. |
| **Ev Sahibi** | Kendi ilanlarını oluşturur/düzenler. Rezervasyon taleplerini kabul/reddeder. Gelir raporu alır. |
| **Kiracı** | İlan arar, rezervasyon yapar, ödeme gerçekleştirir, yorum bırakır. |

### ⚙️ Temel Fonksiyonlar
- 🔐 JWT tabanlı kimlik doğrulama
- 🏠 Tiny house ilan yönetimi (fotoğraf, özellikler, fiyat, kapasite)
- 📅 Tarih bazlı rezervasyon sistemi (çakışma kontrolü trigger ile)
- 💳 Online ödeme kaydı
- ⭐ Yorum ve puanlama sistemi (1-5 yıldız)
- 🔔 Otomatik bildirim sistemi
- 📊 Admin dashboard ve gelir raporları
- ❤️ Favorilere ekleme

---

## 🛠️ Teknolojiler

| Katman | Teknoloji |
|--------|-----------|
| **Backend** | Java 21, Spring Boot 3.x |
| **Güvenlik** | Spring Security, JWT |
| **Veritabanı** | PostgreSQL 16 |
| **ORM** | Spring Data JPA / Hibernate |
| **Frontend** | HTML5, CSS3, JavaScript, Thymeleaf |
| **Build** | Maven |
| **IDE** | IntelliJ IDEA |

---

## 🗄️ Veritabanı Tasarımı

### Tablolar
```
users · tiny_houses · tiny_house_images · availability_calendar
reservations · payments · reviews · notifications · favorites
```

### Stored Procedures
| Prosedür | Açıklama |
|----------|----------|
| `sp_create_reservation` | Müsaitlik kontrolü, fiyat hesabı ve rezervasyon oluşturma |
| `sp_complete_payment` | Ödeme kaydı oluşturma ve rezervasyon güncelleme |

### Functions
| Fonksiyon | Açıklama |
|-----------|----------|
| `fn_calculate_average_rating` | İlan ortalama puanını hesaplar |
| `fn_calculate_total_revenue` | Ev sahibi/platform toplam gelirini hesaplar |

### Triggers
| Trigger | Açıklama |
|---------|----------|
| `trg_review_rating_update` | Yorum eklenince/silinince ortalama puanı otomatik günceller |
| `trg_reservation_overlap_check` | Aynı eve aynı tarihe çakışan rezervasyonu engeller |

---

## ⚡ Kurulum

### Gereksinimler
- Java 21+
- PostgreSQL 14+
- Maven 3.8+

### 1. Veritabanı Kurulumu
```bash
# PostgreSQL'de veritabanı oluştur
CREATE DATABASE tinyhousedb;

# SQL scriptlerini sırayla çalıştır
psql -U postgres -d tinyhousedb -f sql/01_schema.sql
psql -U postgres -d tinyhousedb -f sql/02_seed_data.sql
```

### 2. Uygulama Konfigürasyonu
`src/main/resources/` klasöründe `application.properties` dosyası oluştur:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tinyhousedb
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

app.jwt.secret=YOUR_JWT_SECRET
app.jwt.access-token-expiration=900000
app.jwt.refresh-token-expiration=604800000
```

### 3. Uygulamayı Başlat
```bash
# Maven ile çalıştır
mvn spring-boot:run

# veya IntelliJ IDEA'da TinyhouseApplication.java → Run
```

### 4. Tarayıcıda Aç
```
http://localhost:8080
```

### 5. Varsayılan Admin Hesabı
```
E-posta : admin@tinyhouse.com
Şifre   : admin123
```

---

## 📁 Proje Yapısı

```
tinyhouse/
├── sql/
│   ├── 01_schema.sql        # Tablo tanımları, constraint'ler
│   ├── 02_seed_data.sql     # Örnek veriler
│   ├── 03_queries.sql       # SP, Function, Trigger
│   └── 04_drop_all.sql      # Temizlik scripti
└── src/
    └── main/
        ├── java/com/tinyhouse/
        │   ├── config/          # Security, Swagger, Web config
        │   ├── controller/      # REST API & Web controller'lar
        │   ├── dto/             # Request & Response DTO'ları
        │   ├── entity/          # JPA Entity sınıfları
        │   ├── enums/           # Rol, durum enum'ları
        │   ├── exception/       # Global exception handler
        │   ├── jwt/             # JWT token yönetimi
        │   ├── mapper/          # Entity ↔ DTO dönüşümleri
        │   ├── repository/      # JPA Repository arayüzleri
        │   ├── scheduler/       # Otomatik görev zamanlayıcı
        │   ├── security/        # Auth servisleri
        │   └── service/         # İş mantığı servisleri
        └── resources/
            ├── static/          # CSS, JS dosyaları
            └── templates/       # Thymeleaf HTML şablonları
                ├── admin/
                ├── owner/
                └── tenant/
```

---

## 📸 Ekranlar

| Admin | Ev Sahibi | Kiracı |
|-------|-----------|--------|
| Dashboard & İstatistikler | İlan Yönetimi | Ana Sayfa & Arama |
| Kullanıcı Yönetimi | Rezervasyon Takibi | Rezervasyon Yapma |
| Rezervasyon & Ödeme Yönetimi | Gelir Raporları | Yorumlar & Puanlama |

---

## 📄 Lisans

Bu proje eğitim amaçlı geliştirilmiştir.

---

<p align="center">
  <b>Çağlar Yılmaz</b> · İleri Veritabanı Dersi · 2025-2026 Bahar
</p>
