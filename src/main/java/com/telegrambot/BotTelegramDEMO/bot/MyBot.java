package com.telegrambot.BotTelegramDEMO.bot;

import com.telegrambot.BotTelegramDEMO.model.User;
import com.telegrambot.BotTelegramDEMO.service.GeminiService;
import com.telegrambot.BotTelegramDEMO.service.JsonStorageService;
import com.telegrambot.BotTelegramDEMO.service.RegistroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MyBot extends TelegramLongPollingBot {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private RegistroService registroService;

    @Autowired
    private JsonStorageService jsonStorage;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String mensaje = update.getMessage().getText().trim();
            String chatId = update.getMessage().getChatId().toString();
            String respuesta;

            try {
                //Inicio del registro
                if (mensaje.equalsIgnoreCase("/start")) {
                    respuesta = registroService.manejarRegistro(chatId, mensaje);

                    //Si el usuario no está registrado, continuar el flujo de registro
                } else if (!estaRegistrado(chatId)) {
                    respuesta = registroService.manejarRegistro(chatId, mensaje);

                    //Usuario ya registrado → procesar consulta con Gemini
                } else {
                    User user = jsonStorage.findByChatId(chatId);
                    String contexto = String.format("""
                            El usuario se llama %s, tiene %d años y su objetivo es %s.
                            Responde de forma amigable y profesional, como un nutricionista que conoce su caso.
                            """, user.getNombre(), user.getEdad(), user.getObjetivo());

                    String promptFinal = contexto + "\n\nConsulta del usuario: " + mensaje;
                    respuesta = geminiService.obtenerRespuesta(promptFinal);
                }

            } catch (Exception e) {
                e.printStackTrace();
                respuesta = "⚠️ Ocurrió un error procesando tu mensaje. Intenta de nuevo.";
            }

            // Enviar mensaje con texto escapado (evita error 400 Bad Request)
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(escapeMarkdown(respuesta));
            message.enableMarkdown(true);

            try {
                execute(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean estaRegistrado(String chatId) {
        return jsonStorage.getAllUsers().stream()
                .anyMatch(u -> u.getChatId().equals(chatId));
    }

    @Override
    public String getBotUsername() {
        return "nutri_demobot";
    }

    @Override
    public String getBotToken() {
        return "7975286377:AAGSgMdkAy2lQ4M7dR2c3AstLNWbpDXuC8Y";
    }

    // 🔹 Evita errores por símbolos especiales en Markdown
    private String escapeMarkdown(String text) {
        if (text == null) return "";
        return text
                .replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("~", "\\~")
                .replace("`", "\\`")
                .replace(">", "\\>")
                .replace("#", "\\#")
                .replace("+", "\\+")
                .replace("-", "\\-")
                .replace("=", "\\=")
                .replace("|", "\\|")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace(".", "\\.")
                .replace("!", "\\!");
    }
}
