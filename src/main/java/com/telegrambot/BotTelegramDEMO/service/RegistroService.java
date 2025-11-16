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

    public void reiniciarRegistro(String chatId) {
        jsonStorage.removeTempUser(chatId);          // temporal
        jsonStorage.removeUser(chatId);              // definitivo
        estados.remove(chatId);
    }


    public String manejarRegistro(String chatId, String mensaje) {

        if (jsonStorage.findByChatId(chatId) != null) {
            return "Ya estás registrado 😊. Si querés reiniciar escribí /registro.";
        }

        String estado = estados.get(chatId);

        if (mensaje.equalsIgnoreCase("/start")) {
            estados.put(chatId, "NOMBRE");
            return "👋 ¡Hola! Soy *NutriBot*. Decime tu *nombre*:";
        }

        if ("NOMBRE".equals(estado)) {
            User u = new User();
            u.setChatId(chatId);
            u.setNombre(mensaje);
            jsonStorage.saveTempUser(u);

            estados.put(chatId, "EDAD");
            return "Perfecto. Ahora tu *edad*:";
        }

        if ("EDAD".equals(estado)) {
            try {
                int edad = Integer.parseInt(mensaje);
                User u = jsonStorage.findTempUser(chatId);
                u.setEdad(edad);
                jsonStorage.saveTempUser(u);

                estados.put(chatId, "PESO");
                return "Genial. Ahora decime tu *peso en kg*:";
            } catch (Exception e) {
                return "Ingresá solo números.";
            }
        }

        if ("PESO".equals(estado)) {
            try {
                double peso = Double.parseDouble(mensaje);
                User u = jsonStorage.findTempUser(chatId);
                u.setPeso(peso);
                jsonStorage.saveTempUser(u);

                estados.put(chatId, "ALTURA");
                return "Anotado 👍. ¿Cuál es tu *altura en cm*?";
            } catch (Exception e) {
                return "Ingresá un número válido.";
            }
        }

        if ("ALTURA".equals(estado)) {
            try {
                double altura = Double.parseDouble(mensaje);
                User u = jsonStorage.findTempUser(chatId);
                u.setAltura(altura);
                jsonStorage.saveTempUser(u);

                estados.put(chatId, "SEXO");
                return "Decime tu *sexo* (masculino/femenino):";
            } catch (Exception e) {
                return "Ingresá un número válido.";
            }
        }

        if ("SEXO".equals(estado)) {
            User u = jsonStorage.findTempUser(chatId);
            u.setSexo(mensaje);
            jsonStorage.saveTempUser(u);

            estados.put(chatId, "ACTIVIDAD");
            return """
                    ¿Cuál es tu *nivel de actividad física*?
                    - Sedentario  
                    - Ligero (1-2 veces/semana)  
                    - Moderado (3-4 veces/semana)  
                    - Intenso (5-6 veces/semana)  
                    """;
        }

        if ("ACTIVIDAD".equals(estado)) {
            User u = jsonStorage.findTempUser(chatId);
            u.setActividad(mensaje);
            jsonStorage.saveTempUser(u);

            estados.put(chatId, "OBJETIVO");
            return "Por último, ¿cuál es tu *objetivo nutricional*?";
        }

        if ("OBJETIVO".equals(estado)) {
            User u = jsonStorage.findTempUser(chatId);
            u.setObjetivo(mensaje);

            jsonStorage.saveUser(u);
            jsonStorage.removeTempUser(chatId);
            estados.remove(chatId);

            return "🎉 *¡Registro completado!* Podés empezar a consultarme lo que quieras.";
        }

        return "No entendí eso. Escribí /start para registrarte.";
    }
}
