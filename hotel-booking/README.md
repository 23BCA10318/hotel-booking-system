# 🏨 Grand Azure Hotel Booking System

A full-stack Hotel Booking System built with **Java** (backend) and **HTML/CSS/JS** (frontend), requiring **zero external dependencies**.

---

## 📁 Project Structure

```
hotel-booking/
├── frontend/
│   └── index.html          # Full SPA frontend (all-in-one)
├── src/main/java/com/hotel/
│   ├── model/
│   │   ├── Room.java       # Room entity with types & amenities
│   │   ├── Booking.java    # Booking with date logic & tax calc
│   │   ├── Guest.java      # Guest information
│   │   └── Invoice.java    # Invoice with payment tracking
│   ├── repository/
│   │   ├── RoomRepository.java    # In-memory room store (7 sample rooms)
│   │   └── BookingRepository.java # In-memory booking store with overlap detection
│   ├── service/
│   │   ├── BookingService.java    # Booking logic, validation, state machine
│   │   └── InvoiceService.java   # Invoice generation & payment
│   ├── controller/
│   │   └── HotelServer.java      # HTTP server with REST API routing
│   └── util/
│       └── JsonUtil.java          # Manual JSON serialization
└── run.sh                  # Build & run script
```

---

## 🚀 Quick Start

### Requirements
- Java 11+ JDK

### Run
```bash
chmod +x run.sh
./run.sh
```

Then open: **http://localhost:8080**

---

## 🌐 REST API Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/rooms` | List all rooms |
| GET | `/api/rooms/{id}` | Get room by ID |
| GET | `/api/rooms?type=DELUXE` | Filter by room type |
| GET | `/api/bookings` | List all bookings |
| GET | `/api/bookings/{id}` | Get booking by ID |
| GET | `/api/bookings?email=x@y.com` | Find bookings by email |
| POST | `/api/bookings` | Create new booking |
| PUT | `/api/bookings/{id}` | Update booking status |
| POST | `/api/invoices` | Generate invoice for booking |
| GET | `/api/invoices/{num}` | Get invoice by number |
| PUT | `/api/invoices/{num}` | Mark invoice as paid |
| GET | `/api/availability?roomId=1&checkIn=...&checkOut=...` | Check availability |

### POST /api/bookings — Request Body
```json
{
  "roomId": 1,
  "checkIn": "2026-04-10",
  "checkOut": "2026-04-13",
  "numGuests": 2,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "phone": "+1 234 567 8900",
  "address": "123 Main St",
  "idType": "PASSPORT",
  "idNumber": "AB123456",
  "specialRequests": "Late check-in"
}
```

### PUT /api/bookings/{id} — Actions
```json
{ "action": "cancel" }
{ "action": "checkin" }
{ "action": "checkout" }
```

---

## 🏷️ Room Types

| Type | Price/Night | Capacity |
|------|------------|----------|
| STANDARD | $89.99 | 2 guests |
| DELUXE | $149.99 | 2–3 guests |
| SUITE | $299–349 | 4 guests |
| PRESIDENTIAL | $799.99 | 6 guests |

---

## ✨ Features

- **Room Management** — 7 pre-loaded rooms across 4 categories with amenities
- **Availability Checking** — Overlap detection prevents double-bookings
- **Multi-step Booking Flow** — Select room → Guest info → Confirm
- **Invoice Generation** — Auto-calculates subtotal, 12% tax, total
- **Payment Tracking** — Mark invoices as PAID with payment method
- **Booking Lifecycle** — CONFIRMED → CHECKED_IN → CHECKED_OUT / CANCELLED
- **Admin Dashboard** — Stats panel + full booking management with check-in/out
- **Guest Search** — Find bookings by email address
- **Responsive UI** — Works on desktop and mobile

---

## 🎨 Frontend Pages

1. **Home** — Hero banner + featured rooms + search bar
2. **Rooms** — All rooms with type filter tabs
3. **Book Now** — 3-step booking wizard with real-time price summary
4. **My Bookings** — Search by email, view status, cancel, generate invoice
5. **Admin** — Stats dashboard + all bookings with check-in/out controls

---

## 🔧 Technical Notes

- Uses Java's built-in `com.sun.net.httpserver.HttpServer` — no external libs needed
- In-memory storage (HashMap) — data resets on server restart
- Manual JSON serialization via `JsonUtil.java`
- CORS headers enabled for local dev
- Frontend served as static files from `/frontend` directory
