fun main() {
    val token = System.getenv("DISCORD_BOT_TOKEN")
        ?: throw IllegalStateException("DISCORD_BOT_TOKEN environment variable is required")
    
    val bot = SpongeBotApplication(token)
    bot.start()
}
