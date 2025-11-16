package com.telegrambot.BotTelegramDEMO.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telegrambot.BotTelegramDEMO.model.User;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class JsonStorageService {

    private static final String FILE_PATH = "src/main/resources/data/users.json";
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, User> tempUsers = new HashMap<>();

    // 🔹 Guarda temporalmente un usuario mientras se registra
    public void saveTempUser(User user) {
        tempUsers.put(user.getChatId(), user);
    }

    public User findTempUser(String chatId) {
        return tempUsers.get(chatId);
    }

    public void removeTempUser(String chatId) {
        tempUsers.remove(chatId);
    }

    public void removeUser(String chatId) {
        List<User> users = getAllUsers();
        users.removeIf(u -> u.getChatId().equals(chatId));
        saveAllUsers(users);
    }


    // 🔹 Guarda el usuario definitivo en el archivo JSON
    public void saveUser(User user) {
        List<User> users = getAllUsers();
        users.removeIf(u -> u.getChatId().equals(user.getChatId()));
        users.add(user);
        saveAllUsers(users);
    }

    // 🔹 Devuelve todos los usuarios registrados
    public List<User> getAllUsers() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                mapper.writeValue(file, new ArrayList<>());
            }
            return mapper.readValue(file, new TypeReference<>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // 🔹 Guarda la lista completa en el archivo JSON
    private void saveAllUsers(List<User> users) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User findByChatId(String chatId) {
        return getAllUsers().stream()
                .filter(u -> u.getChatId().equals(chatId))
                .findFirst()
                .orElse(null);
    }
}
