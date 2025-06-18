package dao;

import model.Reminder
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.Timestamp
import java.time.LocalDateTime
import database.DatabaseManager

class ReminderDao(private val databaseManager: DatabaseManager) {
    private val logger = LoggerFactory.getLogger(ReminderDao::class.java)
    
    fun createReminder(reminder: Reminder): Long {
        databaseManager.getConnection().use { connection ->
            val sql = """
                INSERT INTO reminders (user_id, channel_id, guild_id, message, remind_at, created_at, sent)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id
            """.trimIndent()
            
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, reminder.userId)
                statement.setString(2, reminder.channelId)
                statement.setString(3, reminder.guildId)
                statement.setString(4, reminder.message)
                statement.setTimestamp(5, Timestamp.valueOf(reminder.remindAt))
                statement.setTimestamp(6, Timestamp.valueOf(reminder.createdAt))
                statement.setBoolean(7, reminder.sent)
                
                val resultSet = statement.executeQuery()
                if (resultSet.next()) {
                    val id = resultSet.getLong(1)
                    logger.info("Created reminder with ID: $id for user: ${reminder.userId}")
                    return id
                } else {
                    throw RuntimeException("Failed to create reminder")
                }
            }
        }
    }
    
    fun getDueReminders(): List<Reminder> {
        databaseManager.getConnection().use { connection ->
            val sql = """
                SELECT id, user_id, channel_id, guild_id, message, remind_at, created_at, sent, sent_at
                FROM reminders
                WHERE sent = FALSE AND remind_at <= ?
                ORDER BY remind_at ASC
            """.trimIndent()
            
            connection.prepareStatement(sql).use { statement ->
                statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()))
                
                val resultSet = statement.executeQuery()
                val reminders = mutableListOf<Reminder>()
                
                while (resultSet.next()) {
                    val reminder = Reminder(
                        id = resultSet.getLong("id"),
                        userId = resultSet.getString("user_id"),
                        channelId = resultSet.getString("channel_id"),
                        guildId = resultSet.getString("guild_id"),
                        message = resultSet.getString("message"),
                        remindAt = resultSet.getTimestamp("remind_at").toLocalDateTime(),
                        createdAt = resultSet.getTimestamp("created_at").toLocalDateTime(),
                        sent = resultSet.getBoolean("sent"),
                        sentAt = resultSet.getTimestamp("sent_at")?.toLocalDateTime()
                    )
                    reminders.add(reminder)
                }
                
                logger.debug("Found ${reminders.size} due reminders")
                return reminders
            }
        }
    }
    
    fun markReminderAsSent(reminderId: Long) {
        databaseManager.getConnection().use { connection ->
            val sql = """
                UPDATE reminders
                SET sent = TRUE, sent_at = ?
                WHERE id = ?
            """.trimIndent()
            
            connection.prepareStatement(sql).use { statement ->
                statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()))
                statement.setLong(2, reminderId)
                
                val updatedRows = statement.executeUpdate()
                if (updatedRows > 0) {
                    logger.info("Marked reminder $reminderId as sent")
                } else {
                    logger.warn("Failed to mark reminder $reminderId as sent - no rows updated")
                }
            }
        }
    }
    
    fun getUserReminders(userId: String): List<Reminder> {
        databaseManager.getConnection().use { connection ->
            val sql = """
                SELECT id, user_id, channel_id, guild_id, message, remind_at, created_at, sent, sent_at
                FROM reminders
                WHERE user_id = ? AND sent = FALSE
                ORDER BY remind_at ASC
            """.trimIndent()
            
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, userId)
                
                val resultSet = statement.executeQuery()
                val reminders = mutableListOf<Reminder>()
                
                while (resultSet.next()) {
                    val reminder = Reminder(
                        id = resultSet.getLong("id"),
                        userId = resultSet.getString("user_id"),
                        channelId = resultSet.getString("channel_id"),
                        guildId = resultSet.getString("guild_id"),
                        message = resultSet.getString("message"),
                        remindAt = resultSet.getTimestamp("remind_at").toLocalDateTime(),
                        createdAt = resultSet.getTimestamp("created_at").toLocalDateTime(),
                        sent = resultSet.getBoolean("sent"),
                        sentAt = resultSet.getTimestamp("sent_at")?.toLocalDateTime()
                    )
                    reminders.add(reminder)
                }
                
                return reminders
            }
        }
    }
}
