package com.hotel.service;

import com.hotel.model.*;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;
import java.time.LocalDate;
import java.util.*;

public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private static int bookingCounter = 1000;

    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    public Booking createBooking(int roomId, Guest guest, LocalDate checkIn,
                                  LocalDate checkOut, int numGuests, String specialRequests) {
        // Validations
        validateDates(checkIn, checkOut);

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));

        if (numGuests > room.getCapacity()) {
            throw new IllegalArgumentException("Number of guests exceeds room capacity of " + room.getCapacity());
        }

        if (!bookingRepository.isRoomAvailable(roomId, checkIn, checkOut)) {
            throw new IllegalArgumentException("Room is not available for the selected dates");
        }

        String bookingId = generateBookingId();
        Booking booking = new Booking(bookingId, room, guest, checkIn, checkOut, numGuests);
        booking.setSpecialRequests(specialRequests);

        return bookingRepository.save(booking);
    }

    public Optional<Booking> getBooking(String bookingId) {
        return bookingRepository.findById(bookingId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getBookingsByEmail(String email) {
        return bookingRepository.findByGuestEmail(email);
    }

    public Booking cancelBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        if (booking.getStatus() == Booking.BookingStatus.CHECKED_IN) {
            throw new IllegalStateException("Cannot cancel a booking that is already checked in");
        }
        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    public Booking checkIn(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        if (booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Booking must be CONFIRMED for check-in");
        }

        booking.setStatus(Booking.BookingStatus.CHECKED_IN);
        roomRepository.updateAvailability(booking.getRoom().getId(), false);
        return bookingRepository.save(booking);
    }

    public Booking checkOut(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        if (booking.getStatus() != Booking.BookingStatus.CHECKED_IN) {
            throw new IllegalStateException("Booking must be CHECKED_IN for check-out");
        }

        booking.setStatus(Booking.BookingStatus.CHECKED_OUT);
        roomRepository.updateAvailability(booking.getRoom().getId(), true);
        return bookingRepository.save(booking);
    }

    public boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut) {
        validateDates(checkIn, checkOut);
        return bookingRepository.isRoomAvailable(roomId, checkIn, checkOut);
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required");
        }
        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }
    }

    private String generateBookingId() {
        return "BK" + String.format("%06d", ++bookingCounter);
    }
}
