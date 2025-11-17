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

    private final Map<String, String> estados = new HashMap<>(); //estado de registro de cada usuario

    public void reiniciarRegistro(String chatId) {
        jsonStorage.removeTempUser(chatId);          //usuario temporal (si se estaba registrando)
        jsonStorage.removeUser(chatId);              //usuario definitivo(si ya estaba registrado)
        estados.remove(chatId);
    }


    public String manejarRegistro(String chatId, String mensaje) {

        if (jsonStorage.findByChatId(chatId) != null) { //verifica si existe en el JSON
            return "Ya estás registrado 😊. Si querés reiniciar escribí /registro.";
        }

        String estado = estados.get(chatId); //toma el estado de registro actual del usuario (en que paso esta)

        if (mensaje.equalsIgnoreCase("/start")) {
            estados.put(chatId, "NOMBRE"); //el usuario (id) actualmente esta en el paso de ingresar su nombre
            return "👋 ¡Hola! Soy *NutriBot*. Decime tu *nombre*:";
        }

        if ("NOMBRE".equals(estado)) { //verifica si el usuario esta en el paso NOMBRE del registro
            User u = new User();
            u.setChatId(chatId); //obtiene su id
            u.setNombre(mensaje); //obtiene su nombre
            jsonStorage.saveTempUser(u); //guarda el usuario temporal

            estados.put(chatId, "EDAD");
            return "Perfecto. Ahora tu *edad*:";
        }

        if ("EDAD".equals(estado)) { //usuario en el paso EDAD del registro
            try {
                int edad = Integer.parseInt(mensaje); //convierte un numero de formato String en int
                User u = jsonStorage.findTempUser(chatId); //busca el usuario mediante su id
                u.setEdad(edad); //obtiene su edad
                jsonStorage.saveTempUser(u);

                estados.put(chatId, "PESO");
                return "Genial. Ahora decime tu *peso en kg*:";
            } catch (Exception e) {
                return "Ingresá solo números."; //en caso de que el usuario ingrese un caracter no numerico
            }
        }

        if ("PESO".equals(estado)) { //usuario en el paso PESO del registro
            try {
                double peso = Double.parseDouble(mensaje); //numero de formato String a double
                User u = jsonStorage.findTempUser(chatId);
                u.setPeso(peso); //obtiene el peso del usuario
                jsonStorage.saveTempUser(u);

                estados.put(chatId, "ALTURA");
                return "Anotado 👍. ¿Cuál es tu *altura en cm*?";
            } catch (Exception e) {
                return "Ingresá un número válido."; //en caso que el usuario ingrese un numero no valido
            }
        }

        if ("ALTURA".equals(estado)) { //usuario en el paso ALTURA del registro
            try {
                double altura = Double.parseDouble(mensaje);
                User u = jsonStorage.findTempUser(chatId);
                u.setAltura(altura); //obtiene la altura del usuario
                jsonStorage.saveTempUser(u);

                estados.put(chatId, "SEXO");
                return "Decime tu *sexo* (masculino/femenino):";
            } catch (Exception e) {
                return "Ingresá un número válido.";
            }
        }

        if ("SEXO".equals(estado)) { //usuario en el paso SEXO del registro
            User u = jsonStorage.findTempUser(chatId);
            u.setSexo(mensaje); //obtiene el sexo del usuario
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

        if ("ACTIVIDAD".equals(estado)) { //usuario en el paso ACTIVIDAD del registro
            User u = jsonStorage.findTempUser(chatId);
            u.setActividad(mensaje); //obtiene el nivel de actividad fisica del usuario
            jsonStorage.saveTempUser(u);

            estados.put(chatId, "OBJETIVO");
            return "Por último, ¿cuál es tu *objetivo nutricional*?";
        }

        if ("OBJETIVO".equals(estado)) { //usuario en el paso OBJETIVO del registro
            User u = jsonStorage.findTempUser(chatId);
            u.setObjetivo(mensaje); //obtiene el objetivo del usuario

            jsonStorage.saveUser(u); //lo guarda como usuario definitivo en el JSON
            jsonStorage.removeTempUser(chatId); //elimina el usuario temporal
            estados.remove(chatId); //limpia el estado de registro

            return "🎉 *¡Registro completado!* Podés empezar a consultarme lo que quieras.";
        }

        return "No entendí eso. Escribí /start para registrarte."; //si el usuario no esta en proceso de registro u otros casos
    }
}
