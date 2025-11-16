package com.telegrambot.BotTelegramDEMO.service;

import com.telegrambot.BotTelegramDEMO.model.User;
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

    public String obtenerRespuesta(User user, String consulta) {
        String promptBase = """
        Eres un asistente virtual especializado en nutrición y bienestar.
        Responde de forma clara y amigable.
        
        Información del usuario:
        - Nombre: %s
        - Edad: %d
        - Peso: %.1f kg
        - Altura: %.1f cm
        - Sexo: %s
        - Actividad física: %s
        - Objetivo: %s

        Consulta del usuario: %s
        """.formatted(
                user.getNombre(),
                user.getEdad(),
                user.getPeso(),
                user.getAltura(),
                user.getSexo(),
                user.getActividad(),
                user.getObjetivo(),
                consulta
        );

        ChatResponse response = chatModel.call(new Prompt(promptBase));
        return response.getResult().getOutput().getText();
    }

}
