package hr.unipu.musclestore.utils

import hr.unipu.musclestore.data.WorkoutNotation
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

object StreakManager {
    fun calculateStreak(notations: List<WorkoutNotation>): Int {
        if (notations.isEmpty()) return 0

        // Parse all timestamps and sort them
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        val dates = notations.map { notation ->
            LocalDateTime.parse(notation.timestamp, formatter).toLocalDate()
        }.distinct().sorted()

        var streak = 1
        for (i in 1 until dates.size) {
            if (dates[i] == dates[i - 1].plusDays(1)) {
                streak++
            } else if (dates[i] != dates[i - 1]) {
                break
            }
        }
        return streak
    }

    fun calculateWeeklyAverage(notations: List<WorkoutNotation>): Double {
        if (notations.isEmpty()) return 0.0

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        val dates = notations.map { notation ->
            LocalDateTime.parse(notation.timestamp, formatter).toLocalDate()
        }

        val currentMonth = LocalDate.now().month
        val currentYear = LocalDate.now().year

        val daysInMonth = LocalDate.of(currentYear, currentMonth, 1).lengthOfMonth()
        val weeksInMonth = (daysInMonth / 7) + if (daysInMonth % 7 != 0) 1 else 0

        val weeklyWorkouts = dates.filter { date ->
            date.month == currentMonth && date.year == currentYear
        }.groupBy { date ->
            date.withDayOfMonth(1).plusWeeks(date.dayOfMonth / 7.toLong())
        }.size

        return ceil(weeklyWorkouts.toDouble() / weeksInMonth)
    }

    fun calculateMonthlyAverage(notations: List<WorkoutNotation>): Double {
        if (notations.isEmpty()) return 0.0

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        val dates = notations.map { notation ->
            LocalDateTime.parse(notation.timestamp, formatter).toLocalDate()
        }

        val startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY)
        val endOfWeek = startOfWeek.plusDays(6)

        val weeklyWorkouts = dates.count { date ->
            !date.isBefore(startOfWeek) && !date.isAfter(endOfWeek)
        }

        val currentMonth = LocalDate.now().month
        val monthsInYear = LocalDate.now().year

        val months = LocalDate.now().withDayOfMonth(1).let { startOfMonth ->
            (0 until 12).map { startOfMonth.plusMonths(it.toLong()) }
        }.filter { month ->
            dates.any { date ->
                date.month == month.month && date.year == month.year
            }
        }.size

        return if (months > 0) weeklyWorkouts.toDouble() / months else 0.0
    }
}
