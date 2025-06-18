package config

import java.io.FileNotFoundException
import java.util.Properties
import org.slf4j.LoggerFactory

object BotConfig {
    private val logger = LoggerFactory.getLogger(BotConfig::class.java)
    private val properties = Properties()
    
    init {
        try {
            val inputStream = this::class.java.classLoader.getResourceAsStream("application.properties")
            if (inputStream != null) {
                properties.load(inputStream)
                logger.info("Loaded configuration from application.properties")
            } else {
                logger.warn("application.properties not found, using default values")
            }
        } catch (e: Exception) {
            logger.error("Failed to load application.properties", e)
        }
    }
      /**
     * Get the advertisement probability (0.0 to 1.0)
     */
    fun getAdvertisementProbability(): Double {
        val defaultValue = 0.075
        return try {
            val value = properties.getProperty("advertisement.probability", defaultValue.toString()).toDouble()
            if (value < 0.0 || value > 1.0) {
                logger.warn("Advertisement probability out of range (0.0-1.0): $value, using default: $defaultValue")
                defaultValue
            } else {
                value
            }
        } catch (e: Exception) {
            logger.warn("Failed to parse advertisement.probability, using default: $defaultValue", e)
            defaultValue
        }
    }
      /**
     * Get the breaking news probability (0.0 to 1.0)
     */
    fun getBreakingNewsProbability(): Double {
        val defaultValue = 0.03
        return try {
            val value = properties.getProperty("breakingnews.probability", defaultValue.toString()).toDouble()
            if (value < 0.0 || value > 1.0) {
                logger.warn("Breaking news probability out of range (0.0-1.0): $value, using default: $defaultValue")
                defaultValue
            } else {
                value
            }
        } catch (e: Exception) {
            logger.warn("Failed to parse breakingnews.probability, using default: $defaultValue", e)
            defaultValue
        }
    }
    
    /**
     * Get the advertisement cooldown size (number of recent ads to remember)
     */
    fun getAdvertisementCooldownSize(): Int {
        val defaultValue = 10
        return try {
            val value = properties.getProperty("advertisement.cooldown.size", defaultValue.toString()).toInt()
            if (value < 1) {
                logger.warn("Advertisement cooldown size must be at least 1: $value, using default: $defaultValue")
                defaultValue
            } else {
                value
            }
        } catch (e: Exception) {
            logger.warn("Failed to parse advertisement.cooldown.size, using default: $defaultValue", e)
            defaultValue
        }
    }
    
    /**
     * Get the breaking news cooldown size (number of recent news to remember)
     */
    fun getBreakingNewsCooldownSize(): Int {
        val defaultValue = 8
        return try {
            val value = properties.getProperty("breakingnews.cooldown.size", defaultValue.toString()).toInt()
            if (value < 1) {
                logger.warn("Breaking news cooldown size must be at least 1: $value, using default: $defaultValue")
                defaultValue
            } else {
                value
            }
        } catch (e: Exception) {
            logger.warn("Failed to parse breakingnews.cooldown.size, using default: $defaultValue", e)
            defaultValue
        }
    }
}
