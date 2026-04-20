package com.hotel.repository;

import com.hotel.model.Room;
import java.math.BigDecimal;
import java.util.*;

public class RoomRepository {
    private static final Map<Integer, Room> rooms = new HashMap<>();
    private static int nextId = 1;

    static {
        // Initialize with sample rooms
        addRoom("101", Room.RoomType.STANDARD, new BigDecimal("89.99"), 2,
            "Cozy standard room with garden view",
            new String[]{"WiFi", "TV", "AC", "Mini Bar"});

        addRoom("102", Room.RoomType.STANDARD, new BigDecimal("89.99"), 2,
            "Comfortable standard room with city view",
            new String[]{"WiFi", "TV", "AC", "Coffee Maker"});

        addRoom("201", Room.RoomType.DELUXE, new BigDecimal("149.99"), 2,
            "Spacious deluxe room with ocean view and king bed",
            new String[]{"WiFi", "TV", "AC", "Mini Bar", "Bathtub", "Balcony"});

        addRoom("202", Room.RoomType.DELUXE, new BigDecimal("149.99"), 3,
            "Elegant deluxe room with premium furnishings",
            new String[]{"WiFi", "TV", "AC", "Mini Bar", "Jacuzzi", "Lounge Area"});

        addRoom("301", Room.RoomType.SUITE, new BigDecimal("299.99"), 4,
            "Luxurious suite with separate living room and panoramic views",
            new String[]{"WiFi", "Smart TV", "AC", "Full Bar", "Jacuzzi", "Butler Service", "Dining Area"});

        addRoom("302", Room.RoomType.SUITE, new BigDecimal("349.99"), 4,
            "Executive suite with private terrace and premium amenities",
            new String[]{"WiFi", "Smart TV", "AC", "Full Bar", "Private Pool", "Butler Service", "Kitchen"});

        addRoom("401", Room.RoomType.PRESIDENTIAL, new BigDecimal("799.99"), 6,
            "The pinnacle of luxury - presidential suite with full floor access",
            new String[]{"WiFi", "Smart TV", "AC", "Full Bar", "Private Pool", "24/7 Butler", "Private Chef", "Helipad Access", "Limo Service"});
    }

    private static void addRoom(String roomNumber, Room.RoomType type, BigDecimal price,
                                 int capacity, String description, String[] amenities) {
        Room room = new Room(nextId, roomNumber, type, price, capacity, description, amenities, true);
        rooms.put(nextId, room);
        nextId++;
    }

    public List<Room> findAll() {
        return new ArrayList<>(rooms.values());
    }

    public Optional<Room> findById(int id) {
        return Optional.ofNullable(rooms.get(id));
    }

    public List<Room> findAvailable() {
        List<Room> available = new ArrayList<>();
        for (Room room : rooms.values()) {
            if (room.isAvailable()) available.add(room);
        }
        return available;
    }

    public List<Room> findByType(Room.RoomType type) {
        List<Room> result = new ArrayList<>();
        for (Room room : rooms.values()) {
            if (room.getType() == type) result.add(room);
        }
        return result;
    }

    public void updateAvailability(int roomId, boolean available) {
        Room room = rooms.get(roomId);
        if (room != null) room.setAvailable(available);
    }

    public Room save(Room room) {
        if (room.getId() == 0) {
            room.setId(nextId++);
        }
        rooms.put(room.getId(), room);
        return room;
    }
}
