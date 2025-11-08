package com.telegrambot.BotTelegramDEMO.service;

import com.telegrambot.BotTelegramDEMO.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RegistroService {

    @Autowired
    private JsonStorageService jsonStorage;

    private final Map<String, String> estados = new HashMap<>();

    public String manejarRegistro(String chatId, String mensaje) {
        // Si el usuario ya está registrado, no repite el registro
        if (jsonStorage.findByChatId(chatId) != null) {
            return "Ya estás registrado 😊. Podés comenzar a hacerme tus consultas sobre nutrición.";
        }

        String estadoActual = estados.get(chatId);

        // Primer paso: iniciar registro
        if (mensaje.equalsIgnoreCase("/start")) {
            estados.put(chatId, "PEDIR_NOMBRE");
            return "👋 ¡Hola! Soy *NutriBot*. Antes de empezar, decime tu *nombre*:";
        }

        // Segundo paso: nombre
        if ("PEDIR_NOMBRE".equals(estadoActual)) {
            User user = new User();
            user.setChatId(chatId);
            user.setNombre(mensaje);
            jsonStorage.saveTempUser(user);
            estados.put(chatId, "PEDIR_EDAD");
            return "Gracias, " + mensaje + ". Ahora decime tu *edad*:";
        }

        // Tercer paso: edad
        if ("PEDIR_EDAD".equals(estadoActual)) {
            try {
                int edad = Integer.parseInt(mensaje);
                User user = jsonStorage.findTempUser(chatId);
                if (user != null) {
                    user.setEdad(edad);
                    jsonStorage.saveTempUser(user);
                }
                estados.put(chatId, "PEDIR_OBJETIVO");
                return "Perfecto 👌. Ahora contame cuál es tu *objetivo nutricional* (por ejemplo: bajar de peso, ganar masa muscular, mantenerte, etc.):";
            } catch (NumberFormatException e) {
                return "⚠️ Por favor ingresá una edad válida (solo números).";
            }
        }

        // Cuarto paso: objetivo
        if ("PEDIR_OBJETIVO".equals(estadoActual)) {
            User user = jsonStorage.findTempUser(chatId);
            if (user != null) {
                user.setObjetivo(mensaje);
                jsonStorage.saveUser(user);  // Guarda en el JSON definitivo
                jsonStorage.removeTempUser(chatId);
                estados.remove(chatId);
                return "¡Registro completado con éxito, " + user.getNombre() + "! 🎉\n" +
                        "Podés empezar a consultarme lo que necesites sobre nutrición 🥗.";
            }
        }

        // Si algo falla
        return "No entendí eso 🤔. Escribí /start para comenzar el registro.";
    }
}
