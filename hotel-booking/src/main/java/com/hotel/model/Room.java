package com.hotel.model;

import java.math.BigDecimal;

public class Room {
    private int id;
    private String roomNumber;
    private RoomType type;
    private BigDecimal pricePerNight;
    private int capacity;
    private String description;
    private String[] amenities;
    private boolean available;
    private String imageUrl;

    public enum RoomType {
        STANDARD, DELUXE, SUITE, PRESIDENTIAL
    }

    public Room() {}

    public Room(int id, String roomNumber, RoomType type, BigDecimal pricePerNight,
                int capacity, String description, String[] amenities, boolean available) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.description = description;
        this.amenities = amenities;
        this.available = available;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public RoomType getType() { return type; }
    public void setType(RoomType type) { this.type = type; }
    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String[] getAmenities() { return amenities; }
    public void setAmenities(String[] amenities) { this.amenities = amenities; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
