# SpongeBot

A Discord bot that creates mocking SpongeBob memes and provides reminder functionality with PostgreSQL persistence.

## Features

- **Mocking Memes**: Create SpongeBob mocking memes with alternating case text
- **Reminders**: Set reminders with flexible time formats, stored in PostgreSQL
- **Random Advertisements**: Bot randomly responds to messages with funny, witty advertisements for imaginary products
- **Breaking News**: Bot occasionally sends urgent German "breaking news" about hilariously mundane everyday events
- **Persistent Storage**: All reminders are saved to database to survive bot restarts

## Setup

### Prerequisites

- Java 8 or higher
- Maven
- Docker (for PostgreSQL)
- Discord Bot Token

### Database Setup

1. Start PostgreSQL using Docker Compose:
```bash
docker-compose up -d
```

This will create a PostgreSQL database with:
- Database: `spongebot`
- Username: `spongebot`
- Password: `password`
- Port: `5432`

### Environment Variables

Set the following environment variables:

```bash
# Required
export DISCORD_BOT_TOKEN="your-discord-bot-token"

# Optional (defaults to docker-compose values)
export DATABASE_URL="jdbc:postgresql://localhost:5432/spongebot"
export DATABASE_USERNAME="spongebot"
export DATABASE_PASSWORD="password"
```

### Build and Run

1. Build the project:
```bash
mvn clean package
```

2. Run the bot:
```bash
java -jar target/spongebot-1.0-SNAPSHOT.jar
```

Or use Maven:
```bash
mvn exec:java
```

## Commands

### `/spongebot`
Creates a mocking SpongeBob meme from your text.

**Usage**: `/spongebot message:Your text here`

### `/remindme`
Sets a reminder with flexible time formats.

**Usage**: `/remindme time:5m message:Take a break`

#### Supported Time Formats:

**Duration formats:**
- `5m` - 5 minutes
- `2h` - 2 hours  
- `3d` - 3 days
- `1w` - 1 week

**Date formats:**
- `31.05` - May 31st (current year, or next year if date has passed)
- `23.10.2025` - October 23rd, 2025
- `31.05 22:00` - May 31st at 10:00 PM
- `23.10.2025 14:30` - October 23rd, 2025 at 2:30 PM

## Automatic Features

### Random Advertisements
The bot automatically listens to all messages in all channels and has a configurable chance (default 7.5%) to respond with a funny advertisement for imaginary products. The advertisements are witty, humorous, and describe fictional products with creative marketing copy.

The service includes a cooldown mechanism to prevent repeating the same advertisement too frequently.

**Configuration**: You can adjust the advertisement settings in `application.properties`:
```properties
# Set probability (0.0 = never, 1.0 = always)
advertisement.probability=0.075
# Number of recent advertisements to remember to avoid repeats
advertisement.cooldown.size=10
```

### Breaking News (German)
The bot also has a lower chance (default 3%) to send urgent German "breaking news" messages about completely mundane everyday events. These messages are formatted as serious news bulletins but describe hilariously trivial situations like finding both socks after laundry or a coffee machine working on Monday morning.

The service includes a cooldown mechanism to prevent repeating the same breaking news too frequently.

**Configuration**: You can adjust the breaking news settings in `application.properties`:
```properties
# Set probability (0.0 = never, 1.0 = always)
breakingnews.probability=0.03
# Number of recent breaking news to remember to avoid repeats
breakingnews.cooldown.size=8
```

Note: The bot will never send both an advertisement and breaking news for the same message - breaking news has priority.

## Database Schema

The bot uses a single table `reminders` with the following structure:

```sql
CREATE TABLE reminders (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    channel_id VARCHAR(255) NOT NULL,
    guild_id VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    remind_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sent BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP NULL
);
```

## Architecture

- **Database Layer**: PostgreSQL with HikariCP connection pooling
- **Data Access**: DAO pattern for clean data access
- **Services**: Business logic separation
- **Time Parsing**: Flexible time format parsing with multiple patterns
- **Scheduled Tasks**: Background reminder checking every minute
- **Migration**: Flyway for database schema management

## Development

### Project Structure

```
src/
├── main/
│   ├── kotlin/
│   │   ├── Main.kt                      # Application entry point
│   │   ├── SpongeBotApplication.kt      # Main bot class
│   │   ├── config/
│   │   │   └── BotConfig.kt            # Configuration management
│   │   ├── database/
│   │   │   └── DatabaseManager.kt      # Database connection management
│   │   ├── dao/
│   │   │   └── ReminderDao.kt          # Data access for reminders
│   │   ├── model/
│   │   │   └── Reminder.kt             # Reminder data model
│   │   └── service/
│   │       ├── AdvertisementService.kt  # Advertisement messages
│   │       ├── BreakingNewsService.kt   # German breaking news messages
│   │       ├── ReminderService.kt       # Reminder business logic
│   │       └── TimeParser.kt            # Time parsing utilities
│   └── resources/
│       ├── application.properties       # Configuration
│       └── db/migration/
│           └── V1__Create_reminders_table.sql  # Database schema
└── test/
    └── kotlin/                         # Test files
```

### Adding New Features

1. Create data models in `model/` package
2. Add database migrations in `resources/db/migration/`
3. Implement DAO classes in `dao/` package
4. Add business logic in `service/` package
5. Register slash commands in `SpongeBotApplication.kt`

## Troubleshooting

### Database Connection Issues

1. Ensure PostgreSQL is running: `docker-compose ps`
2. Check database credentials in environment variables
3. Verify database URL format: `jdbc:postgresql://localhost:5432/spongebot`

### Migration Issues

- Flyway will automatically create and migrate the database schema
- If you encounter migration errors, check the `flyway_schema_history` table
- For development, you can reset the database: `docker-compose down -v && docker-compose up -d`

### Bot Permission Issues

Ensure your Discord bot has the following permissions:
- Send Messages
- Use Slash Commands
- Attach Files (for memes)
- Mention Users (for reminders)

## License

This project is open source and available under the MIT License.
