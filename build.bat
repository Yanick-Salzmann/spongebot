@echo off
echo Building SpongeBot standalone JAR...
echo.

echo Step 1: Cleaning previous builds...
mvn clean

echo.
echo Step 2: Compiling and packaging with dependencies...
mvn package

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Build successful!
    echo.
    echo Standalone JAR created: target\spongebot-1.0-SNAPSHOT.jar
    echo.
    echo To run the bot:
    echo   java -jar target\spongebot-1.0-SNAPSHOT.jar
    echo.
    echo Make sure to set DISCORD_BOT_TOKEN environment variable first!
) else (
    echo.
    echo ❌ Build failed! Check the output above for errors.
)

pause
