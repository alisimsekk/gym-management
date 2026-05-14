# Gym Management — Backend API

Spor salonu yönetimi için geliştirilmiş REST tabanlı backend uygulamasıdır. Öğrenci (trainee), antrenör (trainer) ve yönetici (admin) rolleri üzerinden kullanıcı yönetimi, antrenman seansları ve antrenör iş yükü raporlaması sunar.

**Temel URL:** `http://localhost:8089/api/v1`  
**Swagger UI:** `http://localhost:8089/api/v1/swagger-ui/index.html`  
**OpenAPI:** `http://localhost:8089/api/v1/v3/api-docs`

---

## Kullanılan Teknolojiler

| Kategori | Teknoloji |
|----------|-----------|
| Dil | Java 25 |
| Framework | Spring Boot 4.0.5 |
| Build aracı | Maven |
| İlişkisel veritabanı | PostgreSQL 16 (Spring Data JPA + Hibernate) |
| NoSQL veritabanı | MongoDB 7/8 (Spring Data MongoDB) |
| Mesaj kuyruğu | Apache ActiveMQ Classic (Spring JMS) |
| Güvenlik | Spring Security, JWT (jjwt), BCrypt |
| Doğrulama | Jakarta Bean Validation |
| API dokümantasyonu | SpringDoc OpenAPI 2.8.5 (Swagger UI) |
| Yardımcı kütüphaneler | Lombok, Jackson |
| Konteyner | Docker, Docker Compose |

---

## Uygulama Özellikleri

### Kullanıcı rolleri

- **TRAINEE (Üye):** Profil yönetimi, atanmış antrenörler, kendi antrenmanlarını görüntüleme ve oluşturma
- **TRAINER (Antrenör):** Profil yönetimi, atanmış öğrenciler, antrenman oluşturma ve iş yükü özeti
- **ADMIN (Yönetici):** Tüm kullanıcılar, antrenman türleri ve antrenmanlar üzerinde tam yetki

### Üye (Trainee) yönetimi

- Public kayıt veya admin tarafından oluşturma
- Profil bilgileri (ad, soyad, e-posta, doğum tarihi, adres)
- Antrenörlere atama ve güncelleme (`ManyToMany` ilişki)
- Aktif/pasif durum yönetimi
- Filtreli arama (JPA Specification)

### Antrenör (Trainer) yönetimi

- Uzmanlık alanı (`TrainingType`) ile kayıt
- Profil CRUD işlemleri
- Aktif/pasif durum toggle
- Öğrenciler tarafından aranabilirlik

### Antrenman türleri (TrainingType)

- Fitness, Yoga, CrossFit gibi uzmanlık alanları
- Admin tarafından oluşturma, güncelleme ve silme
- Filtreli arama

### Antrenman seansları (Training)

- Öğrenci, antrenör, antrenman türü, tarih ve süre bilgisi
- Aynı antrenör–öğrenci–tür–tarih kombinasyonunun tekrar eklenmesini engelleme
- Antrenman oluşturulduğunda öğrenciye antrenörün otomatik atanması
- Rol bazlı yetkilendirme (admin veya ilgili katılımcılar)

### Antrenör iş yükü raporlama

- Antrenman ekleme/silme işlemleri ActiveMQ kuyruğuna mesaj gönderir
- Consumer, MongoDB'deki `trainer_workload` koleksiyonunu yıl/ay bazında günceller
- REST API üzerinden aylık toplam antrenman süresi özeti

### Kimlik doğrulama ve güvenlik

- JWT tabanlı oturum yönetimi (Bearer token)
- BCrypt ile şifre hashleme
- Logout ile token geçersiz kılma
- Başarısız giriş denemesi sınırı (3 deneme → 5 dakika blok)
- Rol tabanlı endpoint erişim kontrolü (`@PreAuthorize`, URL seviyesi güvenlik)
- CORS desteği (varsayılan: `http://localhost:5173`)

### Başlangıç verisi (Seed)

İlk çalıştırmada PostgreSQL boşsa JSON dosyalarından örnek admin, antrenör, öğrenci, antrenman türü ve antrenman verileri yüklenir.

| Rol | Örnek kullanıcı adı |
|-----|---------------------|
| Admin | `admin.admin` |
| Antrenör | `john.smith`, `emma.wilson`, `michael.brown` |
| Öğrenci | `sarah.johnson`, `david.miller` |

---

## Mimari

```
Controller → Service → Repository
     ↓
  DTO + Converter pattern
     ↓
  JPA Specification (dinamik arama)
```

**Hibrit veri modeli:**

- Kullanıcılar, antrenmanlar ve antrenman türleri → **PostgreSQL**
- Antrenör iş yükü raporları → **MongoDB**
- Antrenman oluşturma/silme sonrası iş yükü güncellemesi → **ActiveMQ** (asenkron)

```
Antrenman oluştur/sil → ActiveMQ kuyruğu → Consumer → MongoDB güncelleme
```

---

## API Endpoint'leri

Tüm yollar `/api/v1` altındadır. Korunan endpoint'lerde `Authorization: Bearer <JWT>` header'ı gereklidir.

### Kimlik doğrulama — `/auth`

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| POST | `/auth/register` | Yeni üye kaydı |
| POST | `/auth/login` | Giriş (JWT döner) |
| PUT | `/auth/change-password` | Şifre değiştirme |
| POST | `/auth/logout` | Çıkış |

### Admin kullanıcı yönetimi — `/admin/users`

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| POST | `/admin/users/trainee` | Üye oluşturma |
| POST | `/admin/users/trainer` | Antrenör oluşturma |
| POST | `/admin/users/admin` | Admin oluşturma |
| GET | `/admin/users/me` | Admin profili |
| PUT | `/admin/users/me` | Admin profil güncelleme |

### Öğrenci — `/trainees`

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/trainees/{username}` | Profil getir |
| GET | `/trainees/id/{id}` | ID ile profil |
| PUT | `/trainees/{id}` | Profil güncelle |
| DELETE | `/trainees/{username}` | Üye sil |
| PATCH | `/trainees/{id}/status` | Aktif/pasif toggle |
| PUT | `/trainees/{username}/update-trainers` | Antrenör ataması güncelle |
| POST | `/trainees/search` | Filtreli arama |

### Antrenörler — `/trainers`

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| POST | `/trainers` | Antrenör kaydı |
| GET | `/trainers/{username}` | Profil getir |
| GET | `/trainers/id/{id}` | ID ile profil |
| PUT | `/trainers/{id}` | Profil güncelle |
| DELETE | `/trainers/{username}` | Antrenör sil |
| PATCH | `/trainers/{id}/status` | Aktif/pasif toggle |
| POST | `/trainers/search` | Filtreli arama |

### Antrenman türleri — `/training-types`

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/training-types` | Tüm türleri listele |
| GET | `/training-types/{id}` | Tek tür getir |
| POST | `/training-types` | Yeni tür oluştur |
| PUT | `/training-types/{id}` | Tür güncelle |
| DELETE | `/training-types/{id}` | Tür sil |
| POST | `/training-types/search` | Filtreli arama |

### Antrenmanlar — `/trainings`

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| POST | `/trainings` | Antrenman oluştur |
| PUT | `/trainings/{id}` | Antrenman güncelle |
| GET | `/trainings/{id}` | Tek antrenman getir |
| GET | `/trainings` | Tüm antrenmanları listele (admin) |
| DELETE | `/trainings/{id}` | Antrenman sil |
| POST | `/trainings/search` | Filtreli arama |

### Raporlar — `/report`

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/report/summary/{trainerUsername}` | Antrenör iş yükü özeti |

---

## Proje Yapısı

```
gym-management/
├── pom.xml
├── Dockerfile
├── docker/
│   └── docker-compose.local.yml    # Yerel Postgres, MongoDB, ActiveMQ
├── deploy/
│   ├── docker-stack.yml            # Prod Docker Swarm stack
│   └── nginx/                      # Reverse proxy
└── src/main/
    ├── java/com/alisimsek/
    │   ├── config/                 # Swagger, ActiveMQ, uygulama ayarları
    │   ├── controller/             # REST controller'lar
    │   ├── converter/              # Entity ↔ DTO dönüşümleri
    │   ├── dto/                    # Request/Response modelleri
    │   ├── enums/                  # UserType, ActionType
    │   ├── exception/              # GlobalExceptionHandler
    │   ├── messaging/              # JMS producer/consumer
    │   ├── model/                  # JPA ve MongoDB entity'leri
    │   ├── repository/             # Veri erişim katmanı
    │   ├── security/               # JWT, SecurityConfig, CORS
    │   ├── service/                # İş mantığı
    │   └── specification/          # JPA Specification (arama)
    └── resources/
        ├── application.yaml
        └── *-data.json             # Seed veri dosyaları
```

---

## Kurulum ve Çalıştırma

### Gereksinimler

- JDK 25
- Maven 3.x (veya `./mvnw`)
- Docker (bağımlı servisler için önerilir)

### 1. Bağımlı servisleri başlat

```bash
docker compose -f docker/docker-compose.local.yml up -d
```

| Servis | Port | Kimlik bilgisi |
|--------|------|----------------|
| PostgreSQL | 15432 | `postgres` / `pass`, DB: `gym-management` |
| MongoDB | 27017 | `root` / `example` |
| Mongo Express | 18081 | `root` / `example` |
| ActiveMQ | 61616, konsol 8161 | `admin` / `admin` |

### 2. Uygulamayı çalıştır

```bash
./mvnw spring-boot:run
```

veya JAR olarak:

```bash
./mvnw -DskipTests package
java -jar target/gym-management-1.0.0.jar
```

Uygulama **8089** portunda dinler.

### Docker image

```bash
docker build -t gym-backend:1.0.0 .
```

---

## Ortam Değişkenleri

| Değişken | Varsayılan | Açıklama |
|----------|------------|----------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:15432/gym-management` | PostgreSQL bağlantı URL'i |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Veritabanı kullanıcısı |
| `SPRING_DATASOURCE_PASSWORD` | `pass` | Veritabanı şifresi |
| `SPRING_DATA_MONGODB_URI` | `mongodb://root:example@localhost:27017/trainer_report?authSource=admin` | MongoDB URI |
| `ACTIVEMQ_BROKER_URL` | `tcp://localhost:61616` | ActiveMQ broker adresi |
| `ACTIVEMQ_USER` / `ACTIVEMQ_PASSWORD` | `admin` / `admin` | ActiveMQ kimlik bilgileri |
| `JWT_SECRET_KEY` | (varsayılan Base64 anahtar) | JWT imza anahtarı |
| `JWT_EXPIRATION` | `3600000` (1 saat) | Token geçerlilik süresi (ms) |
| `CORS_ALLOWED_ORIGINS_0` | `http://localhost:5173` | İzin verilen CORS origin |

---

## İlgili Proje

Frontend uygulaması [`gym-management-ui`](../gym-management-ui/) klasöründedir.
