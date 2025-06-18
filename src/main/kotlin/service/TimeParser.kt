package service;

import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.regex.Pattern

class TimeParser {
    private val logger = LoggerFactory.getLogger(TimeParser::class.java)
    
    companion object {
        // Duration patterns: 5m, 10h, 2d, 1w
        private val DURATION_PATTERN = Pattern.compile("^(\\d+)([mhdw])$", Pattern.CASE_INSENSITIVE)
        
        // Date patterns: 31.05, 23.10.2025, 31.05 22:00, 23.10.2025 14:30
        private val DATE_ONLY_PATTERN = Pattern.compile("^(\\d{1,2})\\.(\\d{1,2})$") // 31.05
        private val DATE_WITH_YEAR_PATTERN = Pattern.compile("^(\\d{1,2})\\.(\\d{1,2})\\.(\\d{4})$") // 23.10.2025
        private val DATE_WITH_TIME_PATTERN = Pattern.compile("^(\\d{1,2})\\.(\\d{1,2})\\s+(\\d{1,2}):(\\d{2})$") // 31.05 22:00
        private val DATE_YEAR_WITH_TIME_PATTERN = Pattern.compile("^(\\d{1,2})\\.(\\d{1,2})\\.(\\d{4})\\s+(\\d{1,2}):(\\d{2})$") // 23.10.2025 14:30
    }
    
    fun parseTime(timeString: String): LocalDateTime? {
        val trimmed = timeString.trim()
        
        // Try parsing as duration first
        parseDuration(trimmed)?.let { return it }
        
        // Try parsing as date/time formats
        parseDateTime(trimmed)?.let { return it }
        
        logger.warn("Could not parse time string: $timeString")
        return null
    }
    
    private fun parseDuration(timeString: String): LocalDateTime? {
        val matcher = DURATION_PATTERN.matcher(timeString)
        if (!matcher.matches()) return null
        
        val amount = matcher.group(1).toLongOrNull() ?: return null
        val unit = matcher.group(2).lowercase()
        
        val now = LocalDateTime.now()
        return when (unit) {
            "m" -> now.plusMinutes(amount)
            "h" -> now.plusHours(amount)
            "d" -> now.plusDays(amount)
            "w" -> now.plusWeeks(amount)
            else -> null
        }
    }
    
    private fun parseDateTime(timeString: String): LocalDateTime? {
        // Try different date/time patterns
        
        // Pattern: 31.05 22:00
        DATE_WITH_TIME_PATTERN.matcher(timeString).let { matcher ->
            if (matcher.matches()) {
                return try {
                    val day = matcher.group(1).toInt()
                    val month = matcher.group(2).toInt()
                    val hour = matcher.group(3).toInt()
                    val minute = matcher.group(4).toInt()
                    
                    val currentYear = LocalDate.now().year
                    val date = LocalDate.of(currentYear, month, day)
                    val time = LocalTime.of(hour, minute)
                    val dateTime = LocalDateTime.of(date, time)
                    
                    // If the date is in the past, assume next year
                    if (dateTime.isBefore(LocalDateTime.now())) {
                        dateTime.plusYears(1)
                    } else {
                        dateTime
                    }
                } catch (e: Exception) {
                    logger.warn("Error parsing date with time: $timeString", e)
                    null
                }
            }
        }
        
        // Pattern: 23.10.2025 14:30
        DATE_YEAR_WITH_TIME_PATTERN.matcher(timeString).let { matcher ->
            if (matcher.matches()) {
                return try {
                    val day = matcher.group(1).toInt()
                    val month = matcher.group(2).toInt()
                    val year = matcher.group(3).toInt()
                    val hour = matcher.group(4).toInt()
                    val minute = matcher.group(5).toInt()
                    
                    val date = LocalDate.of(year, month, day)
                    val time = LocalTime.of(hour, minute)
                    LocalDateTime.of(date, time)
                } catch (e: Exception) {
                    logger.warn("Error parsing full date with time: $timeString", e)
                    null
                }
            }
        }
        
        // Pattern: 31.05
        DATE_ONLY_PATTERN.matcher(timeString).let { matcher ->
            if (matcher.matches()) {
                return try {
                    val day = matcher.group(1).toInt()
                    val month = matcher.group(2).toInt()
                    
                    val currentYear = LocalDate.now().year
                    val date = LocalDate.of(currentYear, month, day)
                    val dateTime = LocalDateTime.of(date, LocalTime.of(9, 0)) // Default to 9 AM
                    
                    // If the date is in the past, assume next year
                    if (dateTime.isBefore(LocalDateTime.now())) {
                        dateTime.plusYears(1)
                    } else {
                        dateTime
                    }
                } catch (e: Exception) {
                    logger.warn("Error parsing date only: $timeString", e)
                    null
                }
            }
        }
        
        // Pattern: 23.10.2025
        DATE_WITH_YEAR_PATTERN.matcher(timeString).let { matcher ->
            if (matcher.matches()) {
                return try {
                    val day = matcher.group(1).toInt()
                    val month = matcher.group(2).toInt()
                    val year = matcher.group(3).toInt()
                    
                    val date = LocalDate.of(year, month, day)
                    LocalDateTime.of(date, LocalTime.of(9, 0)) // Default to 9 AM
                } catch (e: Exception) {
                    logger.warn("Error parsing date with year: $timeString", e)
                    null
                }
            }
        }
        
        return null
    }
    
    fun formatDateTime(dateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        return dateTime.format(formatter)
    }
}
