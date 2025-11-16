package com.telegrambot.BotTelegramDEMO.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String chatId;
    private String nombre;
    private String sexo;
    private double altura;
    private double peso;
    private int edad;
    private String actividad;
    private String objetivo;
}
