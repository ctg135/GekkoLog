package salabaev.gekkolog.ui.utils

import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class NiceDateFormatter {

    companion object
    {
        fun getNiceAge(birthDateMillis: Long?): String {

            if (birthDateMillis == null)
                return ""

            val today = Calendar.getInstance()
            val birthDate = Calendar.getInstance().apply { timeInMillis = birthDateMillis }

            // Разница в днях
            val diffInMillis = today.timeInMillis - birthDate.timeInMillis
            val days = abs(TimeUnit.MILLISECONDS.toDays(diffInMillis))

            return when {
                days == (0).toLong() -> "Новенький"
                days < 30 -> formatDays(days)
                days < 365 -> formatMonthsAndWeeks(days)
                else -> formatYearsAndMonths(birthDate, today)
            }
        }

        // Малыш
        private fun formatDays(days: Long): String {
            val weeks = days / 7
            val remainingDays = days % 7

            return when {
                weeks > 0 && remainingDays > 0 -> "$weeks ${getWeekWord(weeks)} $remainingDays ${getDayWord(remainingDays)}"
                weeks > 0 -> "$weeks ${getWeekWord(weeks)}"
                else -> "$days ${getDayWord(days)}"
            }
        }

        // Крепыш
        private fun formatMonthsAndWeeks(days: Long): String {
            val months = days / 30
            val remainingDays = days % 30
            val weeks = remainingDays / 7

            return when {
                months > 0 && weeks > 0 -> "$months ${getMonthWord(months)} $weeks ${getWeekWord(weeks)}"
                months > 0 -> "$months ${getMonthWord(months)}"
                else -> formatDays(days) // На случай если days близко к 30
            }
        }

        // Взрослый
        private fun formatYearsAndMonths(birthDate: Calendar, today: Calendar): String {
            var years = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)
            var months = today.get(Calendar.MONTH) - birthDate.get(Calendar.MONTH)

            if (months < 0) {
                years--
                months += 12
            }

            return when {
                years > 0 && months > 0 -> "$years ${getYearWord(years.toLong())} $months ${getMonthWord(months.toLong())}"
                years > 0 -> "$years ${getYearWord(years.toLong())}"
                else -> "$months ${getMonthWord(months.toLong())}"
            }
        }

        // Функции для склонения слов
        private fun getDayWord(count: Long): String {
            return when {
                count % 10 == 1L && count % 100 != 11L -> "день"
                count % 10 in 2..4 && count % 100 !in 12..14 -> "дня"
                else -> "дней"
            }
        }

        private fun getWeekWord(count: Long): String {
            return when {
                count % 10 == 1L && count % 100 != 11L -> "неделя"
                count % 10 in 2..4 && count % 100 !in 12..14 -> "недели"
                else -> "недель"
            }
        }

        private fun getMonthWord(count: Long): String {
            return when {
                count % 10 == 1L && count % 100 != 11L -> "месяц"
                count % 10 in 2..4 && count % 100 !in 12..14 -> "месяца"
                else -> "месяцев"
            }
        }

        private fun getYearWord(count: Long): String {
            return when {
                count % 10 == 1L && count % 100 != 11L -> "год"
                count % 10 in 2..4 && count % 100 !in 12..14 -> "года"
                else -> "лет"
            }
        }
    }
}