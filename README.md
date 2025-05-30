# SpongeBot 🧽

A Discord bot that creates mocking SpongeBob memes using the `/spongebot` slash command.

## Features

- Responds to `/spongebot <message>` slash command
- Converts your message to alternating caps (mOcKiNg TeXt)
- Overlays the text on a mocking SpongeBob image
- Sends the meme directly to the channel

## Setup

### 1. Create a Discord Application

1. Go to [Discord Developer Portal](https://discord.com/developers/applications)
2. Click "New Application"
3. Give it a name (e.g., "SpongeBot")
4. Go to the "Bot" section
5. Click "Add Bot"
6. Copy the bot token

### 2. Configure Bot Permissions

In the Discord Developer Portal:
1. Go to "OAuth2" → "URL Generator"
2. Select scopes: `bot` and `applications.commands`
3. Select bot permissions: 
   - Send Messages
   - Use Slash Commands
   - Attach Files
4. Copy the generated URL and use it to invite the bot to your server

### 3. Set Environment Variable

Set your Discord bot token as an environment variable:

**Windows (Command Prompt):**
```cmd
set DISCORD_BOT_TOKEN=your_actual_bot_token_here
```

**Windows (PowerShell):**
```powershell
$env:DISCORD_BOT_TOKEN="your_actual_bot_token_here"
```

**Linux/Mac:**
```bash
export DISCORD_BOT_TOKEN=your_actual_bot_token_here
```

### 4. Build and Run

#### Option A: Run with Maven (Development)
```cmd
mvn clean compile
mvn exec:java
```

#### Option B: Build Standalone JAR (Production)
```cmd
build.bat
```
This creates a standalone JAR file with all dependencies included.

#### Option C: Run Standalone JAR
```cmd
run-jar.bat
```
Or manually:
```cmd
java -jar target\spongebot-1.0-SNAPSHOT.jar
```

## Distribution

The standalone JAR (`target\spongebot-1.0-SNAPSHOT.jar`) contains everything needed to run the bot. You can copy this single file to any machine with Java 8+ and run it with just:

```cmd
set DISCORD_BOT_TOKEN=your_token_here
java -jar spongebot-1.0-SNAPSHOT.jar
```

## Usage

Once the bot is running and added to your Discord server:

1. Type `/spongebot` in any channel
2. Enter your message when prompted
3. The bot will create and send a mocking SpongeBob meme!

## Example

Input: `Hello World`
Output: A mocking SpongeBob image with text: `hElLo WoRlD`

## Dependencies

- JDA (Java Discord API) 5.0.0-beta.24
- Kotlin 2.1.20
- OkHttp 4.12.0 (for image downloading)
- Logback (for logging)

## License

This project is for educational purposes. SpongeBob SquarePants is owned by Nickelodeon/Viacom.
