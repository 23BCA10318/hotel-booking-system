package com.hotel.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Invoice {
    private String invoiceNumber;
    private Booking booking;
    private LocalDate issueDate;
    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private PaymentStatus paymentStatus;
    private String paymentMethod;

    public enum PaymentStatus {
        PENDING, PAID, PARTIALLY_PAID, REFUNDED
    }

    public Invoice() {}

    public Invoice(String invoiceNumber, Booking booking) {
        this.invoiceNumber = invoiceNumber;
        this.booking = booking;
        this.issueDate = LocalDate.now();
        this.taxRate = new BigDecimal("0.12");
        this.paymentStatus = PaymentStatus.PENDING;
        calculateAmounts();
    }

    public void calculateAmounts() {
        if (booking != null && booking.getRoom() != null) {
            long nights = booking.getNumberOfNights();
            this.subtotal = booking.getRoom().getPricePerNight()
                .multiply(BigDecimal.valueOf(nights))
                .setScale(2, java.math.RoundingMode.HALF_UP);
            this.taxAmount = subtotal.multiply(taxRate)
                .setScale(2, java.math.RoundingMode.HALF_UP);
            this.totalAmount = subtotal.add(taxAmount)
                .setScale(2, java.math.RoundingMode.HALF_UP);
        }
    }

    // Getters and Setters
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
