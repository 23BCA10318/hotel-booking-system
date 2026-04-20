package com.hotel.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Booking {
    private String bookingId;
    private Room room;
    private Guest guest;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfGuests;
    private BookingStatus status;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private String specialRequests;
    private LocalDate bookingDate;

    public enum BookingStatus {
        PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED
    }

    public Booking() {}

    public Booking(String bookingId, Room room, Guest guest, LocalDate checkInDate,
                   LocalDate checkOutDate, int numberOfGuests) {
        this.bookingId = bookingId;
        this.room = room;
        this.guest = guest;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
        this.status = BookingStatus.CONFIRMED;
        this.bookingDate = LocalDate.now();
        calculateAmounts();
    }

    public long getNumberOfNights() {
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    public void calculateAmounts() {
        if (room != null && checkInDate != null && checkOutDate != null) {
            long nights = getNumberOfNights();
            BigDecimal subtotal = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));
            this.taxAmount = subtotal.multiply(new BigDecimal("0.12")).setScale(2, java.math.RoundingMode.HALF_UP);
            this.totalAmount = subtotal.add(taxAmount).setScale(2, java.math.RoundingMode.HALF_UP);
        }
    }

    // Getters and Setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    public Guest getGuest() { return guest; }
    public void setGuest(Guest guest) { this.guest = guest; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    public int getNumberOfGuests() { return numberOfGuests; }
    public void setNumberOfGuests(int numberOfGuests) { this.numberOfGuests = numberOfGuests; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }
}
