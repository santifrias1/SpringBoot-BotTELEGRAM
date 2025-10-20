package com.telegrambot.BotTelegramDEMO.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final ChatModel chatModel;

    public GeminiService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String obtenerRespuesta(String promptUsuario) {
        try {
            String promptBase = """
                Eres un asistente virtual especializado en nutrición y bienestar.
                Responde siempre con información clara, amigable y basada en evidencia científica.
                Si el usuario pregunta algo fuera de nutrición, puedes responder brevemente
                pero intenta guiarlo nuevamente hacia temas de alimentación saludable.

                Usuario: %s
                """.formatted(promptUsuario);

            ChatResponse response = chatModel.call(new Prompt(promptBase));
            return response.getResult().getOutput().getText(); // 👈 cambio importante

        } catch (Exception e) {
            System.err.println("Error al conectar con Gemini: " + e.getMessage());
            e.printStackTrace();
            return "⚠️ No pude contactar con Gemini. Verifica la API key o el modelo configurado.";
        }
    }
}
