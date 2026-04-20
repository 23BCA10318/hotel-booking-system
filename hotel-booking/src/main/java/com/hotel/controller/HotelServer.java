package com.hotel.controller;

import com.hotel.model.*;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.service.BookingService;
import com.hotel.service.InvoiceService;
import com.hotel.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Executors;

public class HotelServer {
    private static final int PORT = 8080;
    private final BookingService bookingService;
    private final InvoiceService invoiceService;
    private final RoomRepository roomRepository;

    public HotelServer() {
        roomRepository = new RoomRepository();
        BookingRepository bookingRepository = new BookingRepository();
        bookingService = new BookingService(bookingRepository, roomRepository);
        invoiceService = new InvoiceService();
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // API Routes
        server.createContext("/api/rooms", this::handleRooms);
        server.createContext("/api/bookings", this::handleBookings);
        server.createContext("/api/invoices", this::handleInvoices);
        server.createContext("/api/availability", this::handleAvailability);

        // Static files
        server.createContext("/", this::handleStatic);

        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        System.out.println("🏨 Grand Azure Hotel Server started on http://localhost:" + PORT);
    }

    private void handleRooms(HttpExchange exchange) throws IOException {
        setCors(exchange);
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("OPTIONS".equals(method)) { sendResponse(exchange, 200, ""); return; }

        try {
            if ("GET".equals(method)) {
                if (path.matches("/api/rooms/\\d+")) {
                    int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
                    Optional<Room> room = roomRepository.findById(id);
                    if (room.isPresent()) {
                        sendJson(exchange, 200, JsonUtil.toJson(room.get()));
                    } else {
                        sendJson(exchange, 404, JsonUtil.error("Room not found"));
                    }
                } else {
                    String query = exchange.getRequestURI().getQuery();
                    List<Room> rooms;
                    if (query != null && query.contains("type=")) {
                        String typeStr = getQueryParam(query, "type");
                        Room.RoomType type = Room.RoomType.valueOf(typeStr.toUpperCase());
                        rooms = roomRepository.findByType(type);
                    } else {
                        rooms = roomRepository.findAll();
                    }
                    sendJson(exchange, 200, JsonUtil.roomListToJson(rooms));
                }
            }
        } catch (Exception e) {
            sendJson(exchange, 500, JsonUtil.error(e.getMessage()));
        }
    }

    private void handleBookings(HttpExchange exchange) throws IOException {
        setCors(exchange);
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("OPTIONS".equals(method)) { sendResponse(exchange, 200, ""); return; }

        try {
            if ("GET".equals(method)) {
                String query = exchange.getRequestURI().getQuery();
                if (path.matches("/api/bookings/[^/]+")) {
                    String id = path.substring(path.lastIndexOf('/') + 1);
                    Optional<Booking> booking = bookingService.getBooking(id);
                    if (booking.isPresent()) {
                        sendJson(exchange, 200, JsonUtil.toJson(booking.get()));
                    } else {
                        sendJson(exchange, 404, JsonUtil.error("Booking not found"));
                    }
                } else if (query != null && query.contains("email=")) {
                    String email = URLDecoder.decode(getQueryParam(query, "email"), "UTF-8");
                    sendJson(exchange, 200, JsonUtil.bookingListToJson(bookingService.getBookingsByEmail(email)));
                } else {
                    sendJson(exchange, 200, JsonUtil.bookingListToJson(bookingService.getAllBookings()));
                }
            } else if ("POST".equals(method)) {
                String body = readBody(exchange);
                Map<String, String> data = parseJson(body);

                Guest guest = new Guest(0,
                    data.getOrDefault("firstName", ""),
                    data.getOrDefault("lastName", ""),
                    data.getOrDefault("email", ""),
                    data.getOrDefault("phone", ""),
                    data.getOrDefault("address", ""),
                    data.getOrDefault("idType", "PASSPORT"),
                    data.getOrDefault("idNumber", "")
                );

                Booking booking = bookingService.createBooking(
                    Integer.parseInt(data.get("roomId")),
                    guest,
                    LocalDate.parse(data.get("checkIn")),
                    LocalDate.parse(data.get("checkOut")),
                    Integer.parseInt(data.getOrDefault("numGuests", "1")),
                    data.getOrDefault("specialRequests", "")
                );

                sendJson(exchange, 201, JsonUtil.toJson(booking));

            } else if ("PUT".equals(method)) {
                String id = path.substring(path.lastIndexOf('/') + 1);
                String body = readBody(exchange);
                Map<String, String> data = parseJson(body);
                String action = data.getOrDefault("action", "");

                Booking booking;
                switch (action) {
                    case "cancel": booking = bookingService.cancelBooking(id); break;
                    case "checkin": booking = bookingService.checkIn(id); break;
                    case "checkout": booking = bookingService.checkOut(id); break;
                    default: sendJson(exchange, 400, JsonUtil.error("Unknown action")); return;
                }
                sendJson(exchange, 200, JsonUtil.toJson(booking));
            }
        } catch (Exception e) {
            sendJson(exchange, 400, JsonUtil.error(e.getMessage()));
        }
    }

    private void handleInvoices(HttpExchange exchange) throws IOException {
        setCors(exchange);
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("OPTIONS".equals(method)) { sendResponse(exchange, 200, ""); return; }

        try {
            if ("POST".equals(method)) {
                String body = readBody(exchange);
                Map<String, String> data = parseJson(body);
                String bookingId = data.get("bookingId");

                Optional<Booking> bookingOpt = bookingService.getBooking(bookingId);
                if (!bookingOpt.isPresent()) {
                    sendJson(exchange, 404, JsonUtil.error("Booking not found"));
                    return;
                }

                // Check if invoice already exists
                Optional<Invoice> existing = invoiceService.getInvoiceByBookingId(bookingId);
                Invoice invoice = existing.orElseGet(() -> invoiceService.generateInvoice(bookingOpt.get()));
                sendJson(exchange, 200, JsonUtil.toJson(invoice));

            } else if ("GET".equals(method)) {
                String invoiceNum = path.substring(path.lastIndexOf('/') + 1);
                Optional<Invoice> invoice = invoiceService.getInvoice(invoiceNum);
                if (invoice.isPresent()) {
                    sendJson(exchange, 200, JsonUtil.toJson(invoice.get()));
                } else {
                    sendJson(exchange, 404, JsonUtil.error("Invoice not found"));
                }
            } else if ("PUT".equals(method)) {
                String invoiceNum = path.substring(path.lastIndexOf('/') + 1);
                String body = readBody(exchange);
                Map<String, String> data = parseJson(body);
                Invoice invoice = invoiceService.markAsPaid(invoiceNum, data.getOrDefault("paymentMethod", "CASH"));
                sendJson(exchange, 200, JsonUtil.toJson(invoice));
            }
        } catch (Exception e) {
            sendJson(exchange, 400, JsonUtil.error(e.getMessage()));
        }
    }

    private void handleAvailability(HttpExchange exchange) throws IOException {
        setCors(exchange);
        if ("OPTIONS".equals(exchange.getRequestMethod())) { sendResponse(exchange, 200, ""); return; }

        try {
            String query = exchange.getRequestURI().getQuery();
            int roomId = Integer.parseInt(getQueryParam(query, "roomId"));
            LocalDate checkIn = LocalDate.parse(getQueryParam(query, "checkIn"));
            LocalDate checkOut = LocalDate.parse(getQueryParam(query, "checkOut"));

            boolean available = bookingService.isRoomAvailable(roomId, checkIn, checkOut);
            sendJson(exchange, 200, "{\"available\":" + available + ",\"roomId\":" + roomId + "}");
        } catch (Exception e) {
            sendJson(exchange, 400, JsonUtil.error(e.getMessage()));
        }
    }

    private void handleStatic(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/")) path = "/index.html";

        File file = new File("frontend" + path);
        if (!file.exists() || !file.isFile()) {
            file = new File("frontend/index.html");
        }

        if (file.exists()) {
            String contentType = getContentType(file.getName());
            byte[] bytes = Files.readAllBytes(file.toPath());
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();
        } else {
            sendResponse(exchange, 404, "Not Found");
        }
    }

    private String getContentType(String filename) {
        if (filename.endsWith(".html")) return "text/html";
        if (filename.endsWith(".css")) return "text/css";
        if (filename.endsWith(".js")) return "application/javascript";
        if (filename.endsWith(".json")) return "application/json";
        return "text/plain";
    }

    private void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        sendResponse(exchange, status, json);
    }

    private void sendResponse(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private void setCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    private String readBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    private String getQueryParam(String query, String key) {
        if (query == null) return null;
        for (String param : query.split("&")) {
            String[] kv = param.split("=", 2);
            if (kv.length == 2 && kv[0].equals(key)) return kv[1];
        }
        return null;
    }

    private Map<String, String> parseJson(String json) {
        Map<String, String> map = new HashMap<>();
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1, json.length() - 1);
        String[] pairs = json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            pair = pair.trim();
            int colonIdx = pair.indexOf(':');
            if (colonIdx < 0) continue;
            String key = pair.substring(0, colonIdx).trim().replaceAll("\"", "");
            String value = pair.substring(colonIdx + 1).trim().replaceAll("^\"|\"$", "");
            map.put(key, value);
        }
        return map;
    }

    public static void main(String[] args) throws IOException {
        new HotelServer().start();
    }
}
