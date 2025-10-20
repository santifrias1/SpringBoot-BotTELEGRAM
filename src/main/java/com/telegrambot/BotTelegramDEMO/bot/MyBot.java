package com.telegrambot.BotTelegramDEMO.bot;

import com.telegrambot.BotTelegramDEMO.service.GeminiService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MyBot extends TelegramLongPollingBot {

    @Autowired
    private GeminiService geminiService;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String mensaje = update.getMessage().getText();
            String respuesta;

            if (mensaje.equalsIgnoreCase("/start")) {
                respuesta = "\uD83C\uDF4E ¡Hola! Soy *NutriBot*, tu asistente virtual de nutrición y bienestar.  \n" +
                        "        \n" +
                        "        \uD83E\uDD57 Puedo ayudarte con cosas como:\n" +
                        "        • Crear planes de comidas equilibrados.  \n" +
                        "        • Explicar conceptos sobre proteínas, carbohidratos y grasas.  \n" +
                        "        • Darte consejos para mejorar tus hábitos alimenticios.  \n" +
                        "        • Calcular requerimientos calóricos aproximados.  \n" +
                        "        \n" +
                        "        \uD83D\uDCAC Escribime lo que quieras saber, por ejemplo:\n" +
                        "        _\"Qué puedo comer si quiero bajar de peso?\"_  \n" +
                        "        _\"Cuántas calorías debería consumir al día?\"_  \n" +
                        "        \n" +
                        "        ✅ *Consejo:* cuanto más específico seas, mejores serán mis respuestas.";
            } else {
                respuesta = geminiService.obtenerRespuesta(mensaje);
            }

            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId().toString());
            message.setText(respuesta);

            try {
                execute(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "javanewdemo_bot";
    }

    @Override
    public String getBotToken() {
        return "8028484561:AAGvbGXQwmZ1guLwCNaSFyO_klI5EFH0YhM";
    }
}
