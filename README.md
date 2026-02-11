# ChannelBanBot

![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-Database-47A248?logo=mongodb&logoColor=white)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)

**ChannelBanBot** is a Telegram bot for democratic community moderation.  
It allows group members to collectively decide whether a spammer or violator should be muted or banned using a transparent voting mechanism.

The bot is designed to be configurable, multilingual, and production-ready.

---

## ✨ Features

- **Democratic moderation**  
  Start a vote to mute or ban a user by replying to their message.

- **Flexible configuration**  
  Configure vote limits, mute duration, and trigger words directly via Telegram commands.

- **Multilingual support**  
  Full support for multiple languages (English and Russian out of the box).

- **Admin-aware behavior**  
  Admins can restrict bot usage to admins only.  
  The bot automatically detects when it gains or loses admin privileges.

- **Smart localization (i18n)**  
  Uses ICU4J for correct pluralization and human-readable time formatting  
  (e.g. “1 vote”, “3 votes”, “5 votes”; “1 minute”, “2 minutes”).

- **Clean architecture**  
  Clear separation between update handling, business logic, and Telegram API execution.

---

## 🧠 How It Works

1. **Add the bot** to a group chat.
2. **Grant admin privileges** (required for mute/ban actions).
3. **Start a vote** by replying to a violator’s message with `@your_bot_name`
   or a configured trigger word.
4. **Vote** using the inline buttons.
5. **Action is applied automatically** once the vote threshold is reached.

---

## 🤖 Commands

### Admin-only commands

- `/help` — Show help and current chat settings
- `/language` — Change the bot language for the chat
- `/limit` — Set the minimum number of votes required
- `/mute_time` — Configure mute duration (from 1 minute to several hours)
- `/lock` — Restrict bot usage to administrators only
- `/vote_ban_word` — Set a custom trigger word for ban voting
- `/vote_mute_word` — Set a custom trigger word for mute voting

---

## 🛠 Technical Requirements

- **Java 21**
- **MongoDB**
- **Telegram Bot Token** (via [@BotFather](https://t.me/BotFather))

---

## 🏗 Architecture

The project follows a layered and modular architecture focused on clear separation of concerns.

### 🔹 Update Processing Flow
Telegram Update → ChannelGuardBot (LongPolling consumer) → UpdateContextResolver → Facade (Command / Callback) → Handlers → Actions (BotAction) → Action Executors → Telegram API

### 🔹 Core Layers

- **context**  
  Resolves and enriches incoming updates with chat state and metadata.

- **facade**  
  Routes updates to the appropriate handler group (commands or callbacks).

- **handler**  
  Contains business logic for specific commands and callbacks.

- **action**  
  Defines outgoing bot actions (send message, edit message, mute user, etc.)  
  Decouples business logic from Telegram API execution.

- **executor**  
  Executes `BotAction` objects using `TelegramClient`.

- **service**  
  Contains business services and integration logic (chat state, voting logic, caching).

- **repository / model**  
  MongoDB persistence layer.

### 🔹 Design Principles

- Clear separation between **what to do** (actions) and **how to execute** (executors)
- No Telegram API calls inside handlers
- Stateless update processing
- Extensible voting system
- Pluggable caching layer (Caffeine)
- Full i18n support with ICU pluralization

---

## 🚀 Installation & Run

### Environment variables

Create a `.env` file in the project root (see `.env.sample`):

```properties
TELEGRAM_API_KEY=your_telegram_bot_token
TELEGRAM_BOT_USERNAME=your_bot_username
MONGO_DB_URL=mongodb://localhost:27017/channel-guard
```

### Build maven
```bash
mvn clean package
```

### Run
```bash
java -jar target/channel-ban-bot-0.0.1-SNAPSHOT.jar
```

### Docker
```bash
docker build -t channel-ban-bot .
docker run -d channel-ban-bot
```

---

## 🧩 Tech Stack
- **Language: Java 21**
- **Framework: Spring Boot 3.4.x**
- **Database: MongoDB (Spring Data MongoDB)**
- **Telegram SDK: TelegramBots Spring Boot Starter 9.3.0**
- **Caching: Caffeine**
- **Localization: Spring MessageSource + ICU4J**
- **Utilities: Lombok**

---

## 📜 License
This project is licensed under the MIT License.