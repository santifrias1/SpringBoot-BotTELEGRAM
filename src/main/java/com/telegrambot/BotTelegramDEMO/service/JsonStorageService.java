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
    private final ObjectMapper mapper = new ObjectMapper(); //convierte objetos java ↔ JSON
    private final Map<String, User> tempUsers = new HashMap<>(); //guarda usuarios temporalmente durante el registro

    //guarda temporalmente un usuario mientras se registra
    public void saveTempUser(User user) {
        tempUsers.put(user.getChatId(), user);
    }

    public User findTempUser(String chatId) {
        return tempUsers.get(chatId);
    }

    public void removeTempUser(String chatId) {
        tempUsers.remove(chatId);
    }

    //elimins al usuario definitivo
    public void removeUser(String chatId) {
        List<User> users = getAllUsers();
        users.removeIf(u -> u.getChatId().equals(chatId)); //remueve el usuario mediante su id
        saveAllUsers(users);
    }


    //guarda el usuario definitivo en el archivo JSON
    public void saveUser(User user) {
        List<User> users = getAllUsers();
        users.removeIf(u -> u.getChatId().equals(user.getChatId())); //elimina el usuario que tenga mismo id que el actual
        users.add(user);
        saveAllUsers(users);
    }

    //devuelve todos los usuarios registrados
    public List<User> getAllUsers() {
        try {
            File file = new File(FILE_PATH); //objeto file apuntando al .json
            if (!file.exists()) {
                file.getParentFile().mkdirs(); //crea carpetas necesarias
                mapper.writeValue(file, new ArrayList<>()); //crea el json con una lista vacia
            }
            return mapper.readValue(file, new TypeReference<>() {}); //convierte JSON en una lista de objetos User
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>(); //devuelve lista vacia en caso de error
        }
    }

    //guarda la lista completa en el archivo JSON
    private void saveAllUsers(List<User> users) {
        try {
            //toma la lista de usuarios, las convierte en JSON y las escribe en el archivo
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //busca usuario mediante su id en la lista de usuarios guardados
    public User findByChatId(String chatId) {
        return getAllUsers().stream()
                .filter(u -> u.getChatId().equals(chatId)) //verifica que coincida el id d chat
                .findFirst()  //devuelve el primero
                .orElse(null); //si no, devuelve null
    }
}
