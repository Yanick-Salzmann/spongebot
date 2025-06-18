package model;

import java.time.LocalDateTime

data class Reminder(
    val id: Long? = null,
    val userId: String,
    val channelId: String,
    val guildId: String,
    val message: String,
    val remindAt: LocalDateTime,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val sent: Boolean = false,
    val sentAt: LocalDateTime? = null
)
