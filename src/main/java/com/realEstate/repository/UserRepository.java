package com.realEstate.repository;

import com.realEstate.model.User;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository{
    private static final String FILE_PATH = "data/users.txt";
    private static final String DELIMITER = "|";
    private static final String TEMP_FILE_PATH = "data/users_temp.txt";


    public UserRepository() {
        ensureFileExists();
    }

    private void ensureFileExists() {
        try {
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdir();
            }
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize data storage", e);
        }
    }

    public User save(User user) {
        if (user.getUserId() == null) {
            user.setUserId(UUID.randomUUID().toString().substring(0, 8));
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            writer.println(convertToFileLine(user));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }


    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                users.add(convertFromFileLine(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public Optional<User> findById(String userId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = convertFromFileLine(line);
                if (user.getUserId().equals(userId)) {
                    return Optional.of(user);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to find user by ID", e);
        }
        return Optional.empty();
    }

    public User update(User user) {
        File inputFile = new File(FILE_PATH);
        File tempFile = new File(TEMP_FILE_PATH);
        boolean userFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                User currentUser = convertFromFileLine(line);
                if (currentUser.getUserId().equals(user.getUserId())) {
                    writer.println(convertToFileLine(user));
                    userFound = true;
                } else {
                    writer.println(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to update user", e);
        }

        if (!userFound) {
            throw new IllegalArgumentException("User not found with ID: " + user.getUserId());
        }

        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            throw new RuntimeException("Failed to update user file");
        }

        return user;
    }

    public Optional<User> findByEmail(String email) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = convertFromFileLine(line);
                if (user.getEmail().equalsIgnoreCase(email)) {
                    return Optional.of(user);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to find user by email", e);
        }
        return Optional.empty();
    }

    public void deleteById(String userId) {
        File inputFile = new File(FILE_PATH);
        File tempFile = new File(TEMP_FILE_PATH);

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                User user = convertFromFileLine(line);
                if (!user.getUserId().equals(userId)) {
                    writer.println(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete user", e);
        }

        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            throw new RuntimeException("Failed to update user file after deletion");
        }
    }

    private String convertToFileLine(User user) {
        return String.join(DELIMITER,
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole());
    }

    private User convertFromFileLine(String line) {
        String[] parts = line.split("\\" + DELIMITER);
        User user = new User();
        user.setUserId(parts[0]);
        user.setName(parts[1]);
        user.setEmail(parts[2]);
        user.setPassword(parts[3]);
        user.setRole(parts[4]);
        return user;
    }
}
