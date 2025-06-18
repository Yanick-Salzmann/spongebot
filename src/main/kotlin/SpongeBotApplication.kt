import java.awt.Color
import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.net.URL
import javax.imageio.ImageIO
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.utils.FileUpload
import org.slf4j.LoggerFactory
import service.AdvertisementService
import service.BreakingNewsService
import config.BotConfig

class SpongeBotApplication(private val token: String) : ListenerAdapter() {
    private val logger = LoggerFactory.getLogger(SpongeBotApplication::class.java)
    private val advertisementService = AdvertisementService()
    private val breakingNewsService = BreakingNewsService()

    fun start() {
        try {
            JDABuilder.createDefault(token)
                    .setActivity(Activity.playing("with mocking memes"))
                    .addEventListeners(this)
                    .build()

            logger.info("SpongeBot is starting...")
        } catch (e: Exception) {
            logger.error("Failed to start bot", e)
        }
    }    override fun onReady(event: ReadyEvent) {
        logger.info("${event.jda.selfUser.name} is ready!")
        logger.info("Advertisement probability: ${BotConfig.getAdvertisementProbability() * 100}%")
        logger.info("Available advertisements: ${advertisementService.getAdvertisementCount()}")
        logger.info("Breaking news probability: ${BotConfig.getBreakingNewsProbability() * 100}%")
        logger.info("Available breaking news: ${breakingNewsService.getBreakingNewsCount()}")

        // Register slash command
        event.jda
                .updateCommands()
                .addCommands(
                        Commands.slash("spongebot", "Create a mocking SpongeBob meme")
                                .addOption(
                                        OptionType.STRING,
                                        "message",
                                        "The message to mock",
                                        true
                                )
                )
                .queue()
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name == "spongebot") {
            event.deferReply().queue()

            val message = event.getOption("message")?.asString ?: return
            val userId = event.user.id
            val userName = event.user.name

            logger.info(
                    "Processing mocking meme request from user: $userName ($userId) with message: \"$message\""
            )

            try {
                logger.debug("Creating meme image for text length: ${message.length} characters")
                val memeImage = createMockingMeme(message)
                val outputStream = ByteArrayOutputStream()
                ImageIO.write(memeImage, "png", outputStream)

                val fileUpload =
                        FileUpload.fromData(outputStream.toByteArray(), "spongebob_meme.png")
                event.hook.sendFiles(fileUpload).queue()

                logger.info("Successfully sent mocking meme to user: $userName ($userId)")
            } catch (e: Exception) {
                logger.error("Error creating meme for user: $userName ($userId)", e)
                event.hook
                        .sendMessage("Sorry, I couldn't create the meme. Please try again.")
                        .queue()
            }
        }
    }

    private fun createMockingMeme(text: String): BufferedImage {
        // Convert text to alternating case (mocking text)
        val mockingText =
                text.toCharArray()
                        .mapIndexed { index, char ->
                            if (index % 2 == 0) char.lowercase() else char.uppercase()
                        }
                        .joinToString("")
        logger.debug("Converted text to mocking case: \"$mockingText\"")

        // Split text into top and bottom if it's too long
        val maxLineLength = 35 // Reasonable character limit per line
        val (topText, bottomText) =
                if (mockingText.length > maxLineLength) {
                    logger.debug(
                            "Text length (${mockingText.length}) exceeds max line length ($maxLineLength), splitting into two lines"
                    )
                    splitTextIntoLines(mockingText, maxLineLength)
                } else {
                    logger.debug("Text fits in single line")
                    "" to mockingText // Empty top text, all text goes to bottom
                }

        // Download the mocking SpongeBob image
        val templateUrl = "https://i.imgflip.com/1otk96.jpg" // Mocking SpongeBob template
        val templateImage =
                try {
                    logger.debug("Downloading template image from: $templateUrl")
                    ImageIO.read(URL(templateUrl))
                } catch (e: Exception) {
                    logger.warn("Failed to download template image, using fallback", e)
                    // Fallback: create a simple colored background if image download fails
                    BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB).apply {
                        createGraphics().apply {
                            color = Color.YELLOW
                            fillRect(0, 0, 500, 500)
                            dispose()
                        }
                    }
                }

        // Create the final image with text overlay
        val finalImage =
                BufferedImage(templateImage.width, templateImage.height, BufferedImage.TYPE_INT_RGB)
        val graphics = finalImage.createGraphics()

        // Enable antialiasing for better text quality
        graphics.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        )
        graphics.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        )
        // Draw the template image
        graphics.drawImage(templateImage, 0, 0, null)

        // Draw text (top and/or bottom)
        // Calculate font size that fits both lines
        val maxWidth = templateImage.width - 40
        val fontSize =
                if (topText.isNotEmpty() && bottomText.isNotEmpty()) {
                    // If we have both lines, use the smaller font size needed for both
                    val topFontSize = calculateFontSize(topText, maxWidth, graphics)
                    val bottomFontSize = calculateFontSize(bottomText, maxWidth, graphics)
                    minOf(topFontSize, bottomFontSize)
                } else if (topText.isNotEmpty()) {
                    calculateFontSize(topText, maxWidth, graphics)
                } else {
                    calculateFontSize(bottomText, maxWidth, graphics)
                }

        if (topText.isNotEmpty()) {
            drawTextWithOutline(
                    graphics,
                    topText,
                    templateImage.width,
                    60,
                    maxWidth,
                    fontSize
            ) // Top position
            logger.debug("Drew top text: \"$topText\" with font size $fontSize")
        }

        if (bottomText.isNotEmpty()) {
            drawTextWithOutline(
                    graphics,
                    bottomText,
                    templateImage.width,
                    templateImage.height - 50,
                    maxWidth,
                    fontSize
            ) // Bottom position
            logger.debug("Drew bottom text: \"$bottomText\" with font size $fontSize")
        }

        graphics.dispose()
        logger.debug("Meme image creation completed")
        return finalImage
    }

    private fun splitTextIntoLines(text: String, maxLineLength: Int): Pair<String, String> {
        val words = text.split(" ")
        val topWords = mutableListOf<String>()
        val bottomWords = mutableListOf<String>()

        var currentLength = 0
        var useTopLine = true

        for (word in words) {
            if (useTopLine && currentLength + word.length + 1 <= maxLineLength) {
                topWords.add(word)
                currentLength += word.length + 1
            } else {
                useTopLine = false
                bottomWords.add(word)
            }
        }

        // If top line is empty, move some words from bottom to top
        if (topWords.isEmpty() && bottomWords.isNotEmpty()) {
            val midPoint = bottomWords.size / 2
            return bottomWords.take(midPoint).joinToString(" ") to
                    bottomWords.drop(midPoint).joinToString(" ")
        }
        return topWords.joinToString(" ") to bottomWords.joinToString(" ")
    }

    private fun drawTextWithOutline(
            graphics: Graphics2D,
            text: String,
            imageWidth: Int,
            yPosition: Int,
            maxWidth: Int,
            fontSize: Int
    ) {
        // Use the provided font size
        val font = Font("Impact", Font.BOLD, fontSize)
        graphics.font = font

        // Calculate text position
        val fontMetrics = graphics.fontMetrics
        val textWidth = fontMetrics.stringWidth(text)
        val x = (imageWidth - textWidth) / 2

        // Draw outline
        graphics.color = Color.BLACK
        for (dx in -2..2) {
            for (dy in -2..2) {
                if (dx != 0 || dy != 0) {
                    graphics.drawString(text, x + dx, yPosition + dy)
                }
            }
        }

        // Draw main text
        graphics.color = Color.WHITE
        graphics.drawString(text, x, yPosition)
    }

    private fun calculateFontSize(text: String, maxWidth: Int, graphics: Graphics2D): Int {
        var fontSize = 40
        var fontMetrics: FontMetrics

        do {
            val font = Font("Impact", Font.BOLD, fontSize)
            fontMetrics = graphics.getFontMetrics(font)
            fontSize--
        } while (fontMetrics.stringWidth(text) > maxWidth && fontSize > 12)

        return fontSize + 1
    }    override fun onMessageReceived(event: MessageReceivedEvent) {
        // Ignore messages from bots (including ourselves)
        if (event.author.isBot) {
            return
        }

        val message = event.message
        val channel = message.channel
        val user = event.author

        logger.debug("Received message from ${user.name} in channel ${channel.name}: ${message.contentDisplay}")
        
        // Check if we should show breaking news (lower probability, checked first)
        val breakingNewsProbability = BotConfig.getBreakingNewsProbability()
        if (breakingNewsService.shouldShowBreakingNews(breakingNewsProbability)) {
            val breakingNews = breakingNewsService.getRandomBreakingNews()

            logger.info("Showing breaking news to user ${user.name} in channel ${channel.name}")

            channel.sendMessage(breakingNews).queue(
                    {
                        logger.debug("Successfully sent breaking news to channel ${channel.name}")
                    },
                    { error ->
                        logger.error("Failed to send breaking news to channel ${channel.name}", error)
                    }
            )
        } else {
            // Check if we should show an advertisement (only if no breaking news was sent)
            val advertisementProbability = BotConfig.getAdvertisementProbability()
            if (advertisementService.shouldShowAdvertisement(advertisementProbability)) {
                val advertisement = advertisementService.getRandomAdvertisement()

                logger.info("Showing advertisement to user ${user.name} in channel ${channel.name}")

                channel.sendMessage(advertisement).queue(
                        {
                            logger.debug("Successfully sent advertisement to channel ${channel.name}")
                        },
                        { error ->
                            logger.error("Failed to send advertisement to channel ${channel.name}", error)
                        }
                )
            }
        }
    }
}
