package com.realEstate.repository;

import com.realEstate.model.Booking;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BookingRepository {

    private static final String FILE_PATH = "data/bookings.txt";

    @PostConstruct
    public void initFileIfMissing() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Booking> findAll() {
        List<Booking> bookings = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 6) continue;  // Changed from 5 to 6

                Booking booking = new Booking();
                booking.setRequestId(parts[0]);
                booking.setBuyerId(parts[1]);
                booking.setPropertyId(parts[2]);
                booking.setMessage(parts[3]);  // Add this line
                booking.setScheduledDate(LocalDate.parse(parts[4]));  // Changed index from 3 to 4
                booking.setStatus(parts[5]);  // Changed index from 4 to 5
                bookings.add(booking);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public void save(Booking booking) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(formatBooking(booking));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(Booking updatedBooking) {
        List<Booking> all = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Booking booking : all) {
                if (booking.getRequestId().equals(updatedBooking.getRequestId())) {
                    writer.write(formatBooking(updatedBooking));
                } else {
                    writer.write(formatBooking(booking));
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteById(String requestId) {
        List<Booking> all = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Booking booking : all) {
                if (!booking.getRequestId().equals(requestId)) {
                    writer.write(formatBooking(booking));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatBooking(Booking booking) {
        return String.join("|",
                booking.getRequestId(),
                booking.getBuyerId(),
                booking.getPropertyId(),
                booking.getMessage(),  // Add this line
                booking.getScheduledDate().toString(),
                booking.getStatus());
    }
}

