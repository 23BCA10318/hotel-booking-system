package com.hotel.service;

import com.hotel.model.*;
import java.util.*;

public class InvoiceService {
    private static final Map<String, Invoice> invoices = new HashMap<>();
    private static int invoiceCounter = 5000;

    public Invoice generateInvoice(Booking booking) {
        String invoiceNumber = "INV" + String.format("%06d", ++invoiceCounter);
        Invoice invoice = new Invoice(invoiceNumber, booking);
        invoices.put(invoiceNumber, invoice);
        return invoice;
    }

    public Optional<Invoice> getInvoice(String invoiceNumber) {
        return Optional.ofNullable(invoices.get(invoiceNumber));
    }

    public Optional<Invoice> getInvoiceByBookingId(String bookingId) {
        return invoices.values().stream()
            .filter(inv -> inv.getBooking().getBookingId().equals(bookingId))
            .findFirst();
    }

    public Invoice markAsPaid(String invoiceNumber, String paymentMethod) {
        Invoice invoice = invoices.get(invoiceNumber);
        if (invoice == null) throw new IllegalArgumentException("Invoice not found: " + invoiceNumber);
        invoice.setPaymentStatus(Invoice.PaymentStatus.PAID);
        invoice.setPaymentMethod(paymentMethod);
        return invoice;
    }

    public String generateInvoiceText(Invoice invoice) {
        Booking booking = invoice.getBooking();
        Guest guest = booking.getGuest();
        Room room = booking.getRoom();

        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append("         GRAND AZURE HOTEL - INVOICE\n");
        sb.append("=".repeat(60)).append("\n\n");
        sb.append(String.format("Invoice #: %-30s Date: %s%n", invoice.getInvoiceNumber(), invoice.getIssueDate()));
        sb.append(String.format("Booking #: %s%n%n", booking.getBookingId()));

        sb.append("GUEST DETAILS:\n");
        sb.append("-".repeat(40)).append("\n");
        sb.append(String.format("Name    : %s%n", guest.getFullName()));
        sb.append(String.format("Email   : %s%n", guest.getEmail()));
        sb.append(String.format("Phone   : %s%n%n", guest.getPhone()));

        sb.append("ROOM DETAILS:\n");
        sb.append("-".repeat(40)).append("\n");
        sb.append(String.format("Room    : %s (%s)%n", room.getRoomNumber(), room.getType()));
        sb.append(String.format("Check-in : %s%n", booking.getCheckInDate()));
        sb.append(String.format("Check-out: %s%n", booking.getCheckOutDate()));
        sb.append(String.format("Nights  : %d%n", booking.getNumberOfNights()));
        sb.append(String.format("Guests  : %d%n%n", booking.getNumberOfGuests()));

        sb.append("BILLING SUMMARY:\n");
        sb.append("-".repeat(40)).append("\n");
        sb.append(String.format("Room Rate  : $%.2f/night%n", room.getPricePerNight()));
        sb.append(String.format("Subtotal   : $%.2f%n", invoice.getSubtotal()));
        sb.append(String.format("Tax (12%%) : $%.2f%n", invoice.getTaxAmount()));
        sb.append("=".repeat(40)).append("\n");
        sb.append(String.format("TOTAL      : $%.2f%n%n", invoice.getTotalAmount()));
        sb.append(String.format("Payment    : %s%n", invoice.getPaymentStatus()));
        sb.append("=".repeat(60)).append("\n");
        sb.append("Thank you for choosing Grand Azure Hotel!\n");

        return sb.toString();
    }
}
