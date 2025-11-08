package com.telegrambot.BotTelegramDEMO.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String chatId;
    private String nombre;
    private int edad;
    private String objetivo;
}
