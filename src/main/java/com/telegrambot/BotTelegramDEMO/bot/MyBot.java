package com.telegrambot.BotTelegramDEMO.bot;

import com.telegrambot.BotTelegramDEMO.model.User;
import com.telegrambot.BotTelegramDEMO.service.GeminiService;
import com.telegrambot.BotTelegramDEMO.service.JsonStorageService;
import com.telegrambot.BotTelegramDEMO.service.RegistroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MyBot extends TelegramLongPollingBot {

    @Value("${bot.username}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private RegistroService registroService;

    @Autowired
    private JsonStorageService jsonStorage;

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) { //verifica si es un msj de texto y no sticker, etc.

            String mensaje = update.getMessage().getText().trim(); //texto que escribio el usuario
            String chatId = update.getMessage().getChatId().toString(); //obtiene id del chat
            String respuesta;

            try {
                if (mensaje.equalsIgnoreCase("/ayuda")) {
                    respuesta = """
                        📌 *Comandos disponibles:*
                        
                        /start – Iniciar registro  
                        /datos – Ver tus datos guardados  
                        /info – Información útil  
                        /registro – Reiniciar registro  
                        /ayuda – Ver esta lista  
                        """;
                    enviar(chatId, respuesta); //envia respuesta al usuario
                    return;
                }

                if (mensaje.equalsIgnoreCase("/datos")) {
                    User u = jsonStorage.findByChatId(chatId); //busca al usuario en el JSON mediante su id
                    if (u == null) {
                        enviar(chatId, "No encontré tus datos, usá /start para registrarte.");
                        return;
                    }

                    respuesta = String.format("""
                        📋 *Tus datos:*

                        👤 Nombre: %s  
                        🎂 Edad: %d  
                        🎯 Objetivo: %s  
                        ⚖️ Peso: %.1f kg  
                        📏 Altura: %.1f cm  
                        🚻 Sexo: %s  
                        🏃 Actividad: %s  
                        """,
                            u.getNombre(), u.getEdad(), u.getObjetivo(),
                            u.getPeso(), u.getAltura(), u.getSexo(), u.getActividad()
                    );

                    enviar(chatId, respuesta);
                    return;
                }

                if (mensaje.equalsIgnoreCase("/info")) {
                    respuesta = """
                        ℹ️ *Información útil*

                        Para calcular calorías uso:
                        - Edad, peso, altura, sexo  
                        - Nivel de actividad  
                        - Tu objetivo nutricional  

                        Además, podés consultarme ideas de comidas, calorías,
                        cómo armar una dieta, etc.
                        """;
                    enviar(chatId, respuesta);
                    return;
                }

                if (mensaje.equalsIgnoreCase("/registro")) {
                    registroService.reiniciarRegistro(chatId); //reinicia los datos/registro del usuario
                    enviar(chatId, "Registro reiniciado. Escribí /start para comenzar.");
                    return;
                }

                if (mensaje.equalsIgnoreCase("/start")) { //inicio de chat (registro)
                    respuesta = registroService.manejarRegistro(chatId, mensaje);

                } else if (!estaRegistrado(chatId)) {
                    respuesta = registroService.manejarRegistro(chatId, mensaje);

                } else {
                    //si el  usuario ya esta registrado → enviar a Gemini
                    User user = jsonStorage.findByChatId(chatId);
                    respuesta = geminiService.obtenerRespuesta(user, mensaje);
                }

                enviar(chatId, respuesta);

            } catch (Exception e) {
                e.printStackTrace(); //si hay un error, lo muestra en consola
                enviar(chatId, "⚠️ Error inesperado.");
            }
        }
    }

    private void enviar(String chatId, String respuesta) {
        SendMessage msg = new SendMessage(); //funcion de libreria telegram bots
        msg.setChatId(chatId);
        msg.setText(escapeMarkdownSimple(respuesta));
        msg.enableMarkdown(true);
        try { execute(msg); } catch (Exception ignored) {} //envia msg a telegram con execute(msg)
    }


    private boolean estaRegistrado(String chatId) {
        return jsonStorage.getAllUsers().stream() //devuelve la lista de usuarios registrados
                .anyMatch(u -> u.getChatId().equals(chatId)); //verifica si se encuentra un Usuario con el id que busca
    }

    @Override
    public String getBotUsername(){ return botUsername; }

    @Override
    public String getBotToken() { return botToken; }

    //evitar errores de texto por los símbolos especiales en Markdown
    private String escapeMarkdownSimple(String text) {
        if (text == null) return "";
        return text
                .replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("`", "\\`");
    }


}
