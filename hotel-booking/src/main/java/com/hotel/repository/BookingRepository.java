package com.hotel.repository;

import com.hotel.model.Booking;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class BookingRepository {
    private static final Map<String, Booking> bookings = new HashMap<>();

    public Booking save(Booking booking) {
        bookings.put(booking.getBookingId(), booking);
        return booking;
    }

    public Optional<Booking> findById(String bookingId) {
        return Optional.ofNullable(bookings.get(bookingId));
    }

    public List<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }

    public List<Booking> findByGuestEmail(String email) {
        return bookings.values().stream()
            .filter(b -> b.getGuest() != null && email.equals(b.getGuest().getEmail()))
            .collect(Collectors.toList());
    }

    public List<Booking> findByRoomId(int roomId) {
        return bookings.values().stream()
            .filter(b -> b.getRoom() != null && b.getRoom().getId() == roomId)
            .collect(Collectors.toList());
    }

    public boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut) {
        return bookings.values().stream()
            .filter(b -> b.getRoom().getId() == roomId)
            .filter(b -> b.getStatus() != Booking.BookingStatus.CANCELLED)
            .noneMatch(b -> datesOverlap(b.getCheckInDate(), b.getCheckOutDate(), checkIn, checkOut));
    }

    private boolean datesOverlap(LocalDate existingIn, LocalDate existingOut,
                                  LocalDate newIn, LocalDate newOut) {
        return !newOut.isAfter(existingIn) == false && !newIn.isAfter(existingOut) == false
            ? false
            : newIn.isBefore(existingOut) && newOut.isAfter(existingIn);
    }

    public void delete(String bookingId) {
        bookings.remove(bookingId);
    }

    public List<Booking> findActiveBookings() {
        return bookings.values().stream()
            .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED
                      || b.getStatus() == Booking.BookingStatus.CHECKED_IN)
            .collect(Collectors.toList());
    }
}
