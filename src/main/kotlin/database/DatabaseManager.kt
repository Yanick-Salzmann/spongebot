package database;

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.util.*
import javax.sql.DataSource

class DatabaseManager {
    private val logger = LoggerFactory.getLogger(DatabaseManager::class.java)
    private val dataSource: DataSource
    
    init {
        val properties = loadProperties()
        dataSource = createDataSource(properties)
        runMigrations()
    }
    
    private fun loadProperties(): Properties {
        val properties = Properties()
        val inputStream = javaClass.classLoader.getResourceAsStream("application.properties")
        if (inputStream != null) {
            properties.load(inputStream)
        }
        
        // Override with environment variables if they exist
        properties.setProperty("database.url", System.getenv("DATABASE_URL") ?: properties.getProperty("database.url"))
        properties.setProperty("database.username", System.getenv("DATABASE_USERNAME") ?: properties.getProperty("database.username"))
        properties.setProperty("database.password", System.getenv("DATABASE_PASSWORD") ?: properties.getProperty("database.password"))
        
        return properties
    }
    
    private fun createDataSource(properties: Properties): DataSource {
        val config = HikariConfig()
        config.jdbcUrl = properties.getProperty("database.url")
        config.username = properties.getProperty("database.username")
        config.password = properties.getProperty("database.password")
        config.driverClassName = properties.getProperty("database.driver")
        config.maximumPoolSize = properties.getProperty("database.pool.size", "10").toInt()
        config.connectionTimeout = 30000
        config.idleTimeout = 600000
        config.maxLifetime = 1800000
        
        logger.info("Connecting to database: ${config.jdbcUrl}")
        return HikariDataSource(config)
    }
    
    private fun runMigrations() {
        try {
            val flyway = Flyway.configure()
                .dataSource(dataSource)
                .load()
            flyway.migrate()
            logger.info("Database migrations completed successfully")
        } catch (e: Exception) {
            logger.error("Failed to run database migrations", e)
            throw e
        }
    }
    
    fun getConnection(): Connection {
        return dataSource.connection
    }
    
    fun close() {
        if (dataSource is HikariDataSource) {
            dataSource.close()
        }
    }
}
