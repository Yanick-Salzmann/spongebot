@echo off
echo Starting SpongeBot from standalone JAR...
echo.

if not exist "target\spongebot-1.0-SNAPSHOT.jar" (
    echo ❌ Standalone JAR not found!
    echo Please run build.bat first to create the JAR file.
    pause
    exit /b 1
)

if "%DISCORD_BOT_TOKEN%"=="" (
    echo ❌ DISCORD_BOT_TOKEN environment variable is not set!
    echo Please set your Discord bot token:
    echo   set DISCORD_BOT_TOKEN=your_actual_bot_token_here
    echo.
    pause
    exit /b 1
)

echo ✅ Starting SpongeBot...
echo.
java -jar target\spongebot-1.0-SNAPSHOT.jar

pause
