# Smart Ration Queue — PDS Capacity-Aware Appointment System

A full-stack, capacity-aware appointment scheduling and queue management system for the Public Distribution System (PDS / Fair Price Shops). Designed for college project demonstrations and viva examinations.

---

## 📁 Project Architecture & Folder Structure

```
d:/Projects/smart-ration-queue/
├── smart-ration-queue-backend/       # Spring Boot 3.x, Java 17, Maven Backend (STS)
│   ├── pom.xml
│   ├── src/main/java/com/smartration/
│   │   ├── SmartRationQueueApplication.java
│   │   ├── config/                   # SecurityConfig, JwtAuthenticationFilter, JwtTokenProvider, DataInitializer
│   │   ├── controller/               # AuthController, QueueSlotController, BookingController, AdminController, DealerController, RationCardController
│   │   ├── dto/                      # Request & Response DTOs
│   │   ├── entity/                   # User, Dealer, RationCard, QueueSlot, Booking
│   │   ├── exception/                # GlobalExceptionHandler, ResourceNotFoundException, BadRequestException
│   │   ├── repository/               # JPA Repositories
│   │   └── service/                  # AuthService, QueueSlotService, BookingService, AdminService
│   └── src/main/resources/
│       ├── application.properties
│       └── application.properties.example
│
├── smart-ration-queue-frontend/      # React 19, Vite, Tailwind CSS Frontend (VS Code)
│   ├── .env
│   ├── .env.example
│   ├── package.json
│   ├── vite.config.js
│   ├── tailwind.config.js
│   └── src/
│       ├── components/               # Navbar, Sidebar, StatusBadge, ConfirmDialog, ErrorMessage, Loading
│       ├── context/                  # AuthContext
│       ├── pages/
│       │   ├── LandingPage.jsx
│       │   ├── LoginPage.jsx
│       │   ├── RegisterPage.jsx
│       │   ├── beneficiary/          # BeneficiaryDashboard, BookSlotPage, BookingConfirmationPage, MyBookingsPage
│       │   ├── dealer/               # DealerDashboard, ManageSlotsPage, DealerProfilePage
│       │   └── admin/                # AdminDashboard, CreateDealerPage, LinkShopPage
│       ├── routes/                   # ProtectedRoute
│       └── services/                 # api.js, authService.js, slotService.js, bookingService.js, dealerService.js, adminService.js
│
└── README.md                         # Complete project documentation & viva guide
```

---

## 🗄️ 1. MySQL Database Setup

1. Make sure your MySQL service (`MySQL80`) is running.
2. Open MySQL Workbench or Command Prompt:
   ```bash
   mysql -u root -p
   ```
3. Create the database:
   ```sql
   CREATE DATABASE IF NOT EXISTS smart_ration_queue;
   USE smart_ration_queue;
   ```
4. Verify your credentials in `smart-ration-queue-backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/smart_ration_queue?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=4351
   ```
   *(Change `spring.datasource.password` if your local MySQL root password is different)*

Hibernate `ddl-auto=update` will automatically map and manage all tables (`users`, `dealers`, `ration_cards`, `queue_slots`, `bookings`) without deleting any data.

---

## ☕ 2. How to Import and Run the Backend in STS (Spring Tool Suite)

1. Launch **Spring Tool Suite (STS)**.
2. Click **File** ➔ **Import...**
3. Select **Maven** ➔ **Existing Maven Projects** and click **Next**.
4. In **Root Directory**, click **Browse...** and select:
   `D:\Projects\smart-ration-queue\smart-ration-queue-backend`
5. Ensure `pom.xml` is checked, then click **Finish**.
6. Wait for Maven to download dependencies (check the progress in bottom-right corner).
7. In **Package Explorer** / **Project Explorer**, expand:
   `src/main/java` ➔ `com.smartration` ➔ right click `SmartRationQueueApplication.java`
8. Select **Run As** ➔ **Spring Boot App** (or press `Alt + Shift + X, B`).
9. Verify the Console displays:
   ```
   Tomcat started on port 8080 (http) with context path ''
   Started SmartRationQueueApplication in X.XXX seconds
   ```

### Running Backend via Command Line (Alternative):
```powershell
cd D:\Projects\smart-ration-queue\smart-ration-queue-backend
mvn spring-boot:run
```

---

## ⚛️ 3. How to Open and Run the Frontend in VS Code

1. Open **Visual Studio Code (VS Code)**.
2. Click **File** ➔ **Open Folder...**
3. Select:
   `D:\Projects\smart-ration-queue\smart-ration-queue-frontend`
4. Open the integrated terminal (`Ctrl + ~` or **Terminal ➔ New Terminal**).
5. Verify `.env` points to your Spring Boot backend:
   ```env
   VITE_API_BASE_URL=http://localhost:8080
   ```
6. Start the development server:
   ```bash
   npm run dev
   ```
7. Click the URL in the terminal or open your browser at:
   `http://localhost:5173`

---

## 🔑 4. Demo Accounts for Project Presentation

On the Login page (`http://localhost:5173/login`), quick-fill buttons are provided to instantly populate credentials:

| Role | Mobile Number | Password | Details |
| :--- | :--- | :--- | :--- |
| **Admin** | `9000000000` | `NewAdmin@123` | System Administrator — Can create dealers, link shops, and monitor metrics |
| **Dealer** | `9876543220` | `dealer123` | Fair Price Shop Dealer — Assigned to `FPS-HYD-002` (Jai Kisan FPS) |
| **Beneficiary** | `9876500001` | `password123` | Beneficiary — Card `RC9876500001`, assigned to `FPS-HYD-001` |

> 💡 **New Beneficiary Registration:**
> Anyone can register at `http://localhost:5173/register` by providing their Name, 10-digit mobile, and password. An active Ration Card is automatically generated and linked to their assigned Fair Price Shop so they can immediately book slots.

---

## 📋 5. REST API Documentation

### Authentication (`/api/auth`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Public | Register beneficiary account (`name`, `mobile`, `password`, optional `cardNumber`, `shopCode`) |
| `POST` | `/api/auth/login` | Public | Authenticate user (`mobile`, `password`). Returns JWT and role (`{"token": "<jwt>", "role": "..."}`) |

### Queue Slots (`/api/queue-slots`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/queue-slots/dealer/{shopCode}` | DEALER / ADMIN | Create a 30-min collection slot (`slotDate`, `startTime`, `endTime`, `capacity`) |
| `GET` | `/api/queue-slots/dealer/{shopCode}` | Authenticated | Retrieve all queue slots for a Fair Price Shop |
| `GET` | `/api/queue-slots/dealer/{shopCode}/date/{date}` | Authenticated | Retrieve available queue slots for a specific date (e.g. `2026-10-15`) |

### Bookings (`/api/bookings`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/bookings/{slotId}` | BENEFICIARY | Book a slot. Generates token (`TKN-...`), increments `bookedCount` safely |
| `GET` | `/api/bookings/my-bookings` | BENEFICIARY | Retrieve the logged-in beneficiary's bookings history |
| `DELETE` | `/api/bookings/{bookingId}` | BENEFICIARY | Cancel a booking. Decrements `bookedCount`, sets status to `CANCELLED` |

### Administration (`/api/admin`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/admin/dealers` | ADMIN | Create a dealer account and shop (`name`, `mobile`, `password`, `shopCode`, `shopName`, `dailyCapacity`) |
| `POST` | `/api/admin/link-dealer` | ADMIN | Link an existing dealer user mobile to a shop code |
| `GET` | `/api/admin/dealers` | ADMIN | List all dealers and Fair Price Shops |
| `GET` | `/api/admin/stats` | ADMIN | System counts: total users, shops, slots, and bookings |
| `POST` | `/api/admin/assign-card` | ADMIN | Assign or link a ration card to a beneficiary user |

### Fair Price Shops & Cards
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/dealers` | Public | List all active Fair Price Shops |
| `GET` | `/api/dealers/{shopCode}` | Public | Get details of a specific shop |
| `GET` | `/api/ration-cards/my-card` | BENEFICIARY | Get the logged-in beneficiary's assigned ration card and shop |

---

### MySQL Connection Error / Access Denied
1. Open `smart-ration-queue-backend/src/main/resources/application.properties`.
2. Verify that `spring.datasource.password` matches your local MySQL password.
3. Confirm MySQL is running:
   ```powershell
   Get-Service -Name *mysql*
   ```



