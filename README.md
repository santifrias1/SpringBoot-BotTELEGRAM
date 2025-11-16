# 🥗 NutriAssist Bot — Asistente Nutricionista con IA

> Un Bot de Telegram desarrollado en **Java** y **Spring Boot** que actúa como un asistente nutricionista inteligente. Utiliza la tecnología **Google Gemini** para ofrecer consultas personalizadas y guiadas sobre nutrición.

---

## 🚀 Características Principales

### 🤖 Asistente Nutricional con IA (Gemini)

El bot utiliza **Google Gemini** para analizar el perfil del usuario y generar respuestas que simulan la consulta de un nutricionista. Adapta las recomendaciones y análisis a los siguientes datos personales:

- ✅ Edad
- ⚖️ Peso
- 📏 Altura
- 👤 Sexo
- 🏃 Nivel de actividad
- 🎯 Objetivo (bajar, mantener o subir peso)

Esto permite responder consultas personalizadas sobre dietas, **IMC** (Índice de Masa Corporal), cálculo de calorías y más.

---

### 📝 Registro Guiado Paso a Paso

El usuario completa su perfil mediante una serie de preguntas secuenciales. Todos los datos se almacenan en un archivo `.json`.

**Campos solicitados:**

1. 📛 Nombre
2. 🎂 Edad
3. ⚖️ Peso
4. 📏 Altura
5. 👤 Sexo
6. 🏃 Nivel de actividad
7. 🎯 Objetivo

---

### 📁 Persistencia de Datos sin Base de Datos

El sistema maneja la persistencia de la información utilizando el sistema de archivos, lo que simplifica su despliegue y gestión.

- 💾 Guarda los perfiles de usuario en: `/data/users.json`
- 📋 Almacena registros temporales durante el proceso de alta (registro) del usuario

---

## 🧠 Funcionamiento Interno

El bot se compone de varios servicios que gestionan el flujo de información y la lógica del negocio:

| Componente | Función Principal |
|------------|-------------------|
| `MyBot` | Recibe los mensajes de Telegram y gestiona los comandos |
| `RegistroService` | Maneja el flujo interactivo de alta del usuario (paso a paso) |
| `JsonStorage` | Guarda y carga los datos de los usuarios desde el archivo JSON |
| `GeminiService` | Prepara el prompt con la información contextual del usuario y llama al modelo de Google Gemini |

> **Nota:** La respuesta del modelo de IA se envía al usuario final con formato Markdown seguro para una presentación óptima.

---

## 🏗️ Estructura Básica del Proyecto

```
src/
└── main/
    ├── java/
    │   └── com.telegrambot.BotTelegramDEMO/
    │       ├── bot/
    │       │   └── MyBot.java
    │       ├── config/
    │       │   └── BotConfig.java
    │       ├── model/
    │       │   └── User.java
    │       ├── service/
    │       │   ├── GeminiService.java
    │       │   ├── JsonStorageService.java
    │       │   └── RegistroService.java
    │       └── BotTelegramDemoApplication.java
    └── resources/
        ├── data/
        │   └── users.json
        └── application.properties
```

---

## 🛠️ Tecnologías Utilizadas

- ☕ **Java**
- 🍃 **Spring Boot**
- 🤖 **Google Gemini AI**
- 💬 **Telegram Bot API**
- 📄 **JSON** para persistencia

---

## 📊 Diagrama de Clases

![Diagrama de Clases](https://imgur.com/a/SC3ryJD)
