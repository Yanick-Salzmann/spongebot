package service;

import dao.ReminderDao
import model.Reminder
import net.dv8tion.jda.api.JDA
import org.slf4j.LoggerFactory
import service.TimeParser
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ReminderService(
    private val reminderDao: ReminderDao,
    private val timeParser: TimeParser,
    private val jda: JDA
) {
    private val logger = LoggerFactory.getLogger(ReminderService::class.java)
    private val scheduler = Executors.newScheduledThreadPool(2)
    
    init {
        // Start the reminder checker - runs every minute
        scheduler.scheduleAtFixedRate(::checkDueReminders, 1, 1, TimeUnit.MINUTES)
        logger.info("Reminder service started - checking for due reminders every minute")
    }
    
    fun createReminder(
        userId: String,
        channelId: String,
        guildId: String,
        timeString: String,
        message: String
    ): String {
        val remindAt = timeParser.parseTime(timeString)
            ?: return "❌ Invalid time format. Please use formats like: 5m, 2h, 3d, 1w, 31.05, 23.10.2025, 31.05 22:00, or 23.10.2025 14:30"
        
        if (remindAt.isBefore(LocalDateTime.now())) {
            return "❌ The reminder time must be in the future!"
        }
        
        val reminder = Reminder(
            userId = userId,
            channelId = channelId,
            guildId = guildId,
            message = message,
            remindAt = remindAt
        )
        
        try {
            val reminderId = reminderDao.createReminder(reminder)
            val formattedTime = timeParser.formatDateTime(remindAt)
            logger.info("Created reminder $reminderId for user $userId at $formattedTime")
            return "✅ Reminder set for **$formattedTime**!\nMessage: *$message*"
        } catch (e: Exception) {
            logger.error("Failed to create reminder for user $userId", e)
            return "❌ Failed to create reminder. Please try again later."
        }
    }
    
    private fun checkDueReminders() {
        try {
            val dueReminders = reminderDao.getDueReminders()
            logger.debug("Checking ${dueReminders.size} due reminders")
            
            for (reminder in dueReminders) {
                sendReminder(reminder)
            }
        } catch (e: Exception) {
            logger.error("Error checking due reminders", e)
        }
    }
    
    private fun sendReminder(reminder: Reminder) {
        try {
            val guild = jda.getGuildById(reminder.guildId)
            if (guild == null) {
                logger.warn("Guild not found for reminder ${reminder.id}: ${reminder.guildId}")
                return
            }
            
            val channel = guild.getTextChannelById(reminder.channelId)
            if (channel == null) {
                logger.warn("Channel not found for reminder ${reminder.id}: ${reminder.channelId}")
                return
            }
            
            val user = jda.getUserById(reminder.userId)
            val userMention = user?.asMention ?: "<@${reminder.userId}>"
            
            val message = "🔔 **Reminder for $userMention**\n${reminder.message}"
            
            channel.sendMessage(message).queue(
                { success ->
                    reminderDao.markReminderAsSent(reminder.id!!)
                    logger.info("Successfully sent reminder ${reminder.id} to user ${reminder.userId}")
                },
                { error ->
                    logger.error("Failed to send reminder ${reminder.id} to user ${reminder.userId}", error)
                }
            )
        } catch (e: Exception) {
            logger.error("Error sending reminder ${reminder.id}", e)
        }
    }
    
    fun getUserReminders(userId: String): List<Reminder> {
        return reminderDao.getUserReminders(userId)
    }
    
    fun shutdown() {
        scheduler.shutdown()
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow()
            }
        } catch (e: InterruptedException) {
            scheduler.shutdownNow()
        }
    }
}
