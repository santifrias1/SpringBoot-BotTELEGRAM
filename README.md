🥗 NutriAssist Bot — Asistente Nutricionista con IA

Bot de Telegram desarrollado en Java + Spring Boot, que funciona como un asistente nutricionista inteligente utilizando Google Gemini.
Permite registrar usuarios, almacenar sus datos en JSON y responder consultas personalizadas de nutrición, dietas, IMC, calorías y más.

🚀 Características principales
🤖 Asistente nutricional con IA (Gemini)

El bot analiza los datos del usuario y responde como nutricionista, adaptándose a:

Edad

Peso

Altura

Sexo

Nivel de actividad

Objetivo (bajar, mantener o subir peso)

📝 Registro guiado paso a paso

El usuario completa su perfil mediante preguntas:

Nombre

Edad

Peso

Altura

Sexo

Nivel de actividad

Objetivo

Todos los datos se guardan en un archivo .json.

📁 Persistencia sin base de datos

Guarda usuarios en /data/users.json

Guarda registros temporales durante el proceso de alta

🧠 ¿Cómo funciona?

MyBot recibe los mensajes y gestiona los comandos.

RegistroService maneja el flujo de alta del usuario.

JsonStorage guarda y carga los datos desde un archivo JSON.

GeminiService prepara el prompt con la información del usuario y llama al modelo de Google Gemini.

La respuesta se envía al usuario con formato Markdown seguro.

Estructura básica del proyecto
src/
 └── main/
     ├── java/com.telegrambot.BotTelegramDEMO/
     │   ├── MyBot.java
     │   ├── BotConfig.java
     │   ├── model/User.java
     │   ├── service/
     │   │      ├── JsonStorage.java
     │   │      ├── RegistroService.java
     │   │      ├── GeminiService.java
     │   └── BotTelegramDemoApplication.java
     └── resources/
         └── application.properties
