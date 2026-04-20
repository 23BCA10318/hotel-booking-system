package com.hotel.util;

import com.hotel.model.*;
import java.util.List;

public class JsonUtil {

    public static String toJson(Room room) {
        if (room == null) return "null";
        String amenities = "[" + String.join(",",
            java.util.Arrays.stream(room.getAmenities())
                .map(a -> "\"" + a + "\"")
                .toArray(String[]::new)) + "]";
        return String.format(
            "{\"id\":%d,\"roomNumber\":\"%s\",\"type\":\"%s\",\"pricePerNight\":%.2f," +
            "\"capacity\":%d,\"description\":\"%s\",\"amenities\":%s,\"available\":%b}",
            room.getId(), room.getRoomNumber(), room.getType(), room.getPricePerNight(),
            room.getCapacity(), escape(room.getDescription()), amenities, room.isAvailable()
        );
    }

    public static String toJson(Guest guest) {
        if (guest == null) return "null";
        return String.format(
            "{\"id\":%d,\"firstName\":\"%s\",\"lastName\":\"%s\",\"email\":\"%s\"," +
            "\"phone\":\"%s\",\"address\":\"%s\",\"idType\":\"%s\",\"idNumber\":\"%s\"}",
            guest.getId(), escape(guest.getFirstName()), escape(guest.getLastName()),
            escape(guest.getEmail()), escape(guest.getPhone()), escape(guest.getAddress()),
            escape(guest.getIdType()), escape(guest.getIdNumber())
        );
    }

    public static String toJson(Booking booking) {
        if (booking == null) return "null";
        return String.format(
            "{\"bookingId\":\"%s\",\"room\":%s,\"guest\":%s,\"checkInDate\":\"%s\"," +
            "\"checkOutDate\":\"%s\",\"numberOfGuests\":%d,\"status\":\"%s\"," +
            "\"totalAmount\":%.2f,\"taxAmount\":%.2f,\"numberOfNights\":%d," +
            "\"specialRequests\":\"%s\",\"bookingDate\":\"%s\"}",
            booking.getBookingId(), toJson(booking.getRoom()), toJson(booking.getGuest()),
            booking.getCheckInDate(), booking.getCheckOutDate(), booking.getNumberOfGuests(),
            booking.getStatus(), booking.getTotalAmount(), booking.getTaxAmount(),
            booking.getNumberOfNights(),
            escape(booking.getSpecialRequests() != null ? booking.getSpecialRequests() : ""),
            booking.getBookingDate()
        );
    }

    public static String toJson(Invoice invoice) {
        if (invoice == null) return "null";
        return String.format(
            "{\"invoiceNumber\":\"%s\",\"booking\":%s,\"issueDate\":\"%s\"," +
            "\"subtotal\":%.2f,\"taxAmount\":%.2f,\"totalAmount\":%.2f," +
            "\"paymentStatus\":\"%s\",\"paymentMethod\":\"%s\"}",
            invoice.getInvoiceNumber(), toJson(invoice.getBooking()),
            invoice.getIssueDate(), invoice.getSubtotal(), invoice.getTaxAmount(),
            invoice.getTotalAmount(), invoice.getPaymentStatus(),
            invoice.getPaymentMethod() != null ? invoice.getPaymentMethod() : ""
        );
    }

    public static String roomListToJson(List<Room> rooms) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < rooms.size(); i++) {
            sb.append(toJson(rooms.get(i)));
            if (i < rooms.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String bookingListToJson(List<Booking> bookings) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < bookings.size(); i++) {
            sb.append(toJson(bookings.get(i)));
            if (i < bookings.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String error(String message) {
        return "{\"error\":\"" + escape(message) + "\"}";
    }

    public static String success(String message) {
        return "{\"message\":\"" + escape(message) + "\"}";
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}
