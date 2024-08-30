package hr.unipu.musclestore.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object TimestampManager {

    // Function to format a timestamp string into a "dd.MM.yyyy" format
    fun formatTimestamp(timestamp: String?): String {
        return try {
            // Parse the timestamp with microseconds
            val parsedDate = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME)

            // Format the date to "dd.MM.yyyy"
            parsedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        } catch (e: Exception) {
            "Invalid Date"
        }
    }
}