#!/bin/bash
echo "🏨 Grand Azure Hotel Booking System"
echo "===================================="

# Find all Java files
JAVA_FILES=$(find src -name "*.java" | tr '\n' ' ')

# Create output directory
mkdir -p out

echo "📦 Compiling Java source files..."
javac -d out $JAVA_FILES

if [ $? -eq 0 ]; then
  echo "✅ Compilation successful!"
  echo ""
  echo "🚀 Starting server on http://localhost:8080"
  echo "   Open your browser and visit: http://localhost:8080"
  echo ""
  echo "API Endpoints:"
  echo "  GET  /api/rooms              - List all rooms"
  echo "  GET  /api/rooms/{id}         - Get room by ID"
  echo "  GET  /api/bookings           - List all bookings"
  echo "  POST /api/bookings           - Create booking"
  echo "  PUT  /api/bookings/{id}      - Update booking (cancel/checkin/checkout)"
  echo "  POST /api/invoices           - Generate invoice"
  echo "  PUT  /api/invoices/{num}     - Mark invoice as paid"
  echo "  GET  /api/availability       - Check room availability"
  echo ""
  cd out && java com.hotel.controller.HotelServer
else
  echo "❌ Compilation failed!"
  exit 1
fi
