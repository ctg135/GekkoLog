package salabaev.gekkolog.ui.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DatePickerHelper {

    fun showDatePickerDialog(
        context: Context,
        initialDate: Long? = null,
        maxDate: Long? = System.currentTimeMillis(), // Ограничиваем будущие даты
        onDateSelected: (date: Long, formattedDate: String) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        initialDate?.let { calendar.timeInMillis = it }

        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, day ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                val formattedDate = formatDate(selectedCalendar.time)
                onDateSelected(selectedCalendar.timeInMillis, formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Устанавливаем максимальную дату (сегодня)
        maxDate?.let { datePickerDialog.datePicker.maxDate = it }

        // datePickerDialog.datePicker.minDate = someMinDate

        datePickerDialog.show()
    }

    private fun formatDate(date: Date): String {
        return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)
    }

    fun formatDate(timestamp: Long): String {
        return formatDate(Date(timestamp))
    }

    fun showDateTimePickerDialog(
        context: Context,
        initialDate: Long? = null,
        onDateTimeSelected: (date: Long, formattedDate: String) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        initialDate?.let { calendar.timeInMillis = it }

        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, day ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                // После выбора даты показываем TimePicker
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        selectedCalendar.apply {
                            set(Calendar.HOUR_OF_DAY, hourOfDay)
                            set(Calendar.MINUTE, minute)
                        }

                        val formattedDateTime = formatDateTime(selectedCalendar.time)
                        onDateTimeSelected(selectedCalendar.timeInMillis, formattedDateTime)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true // 24-часовой формат
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // datePickerDialog.datePicker.minDate = someMinDate

        datePickerDialog.show()
    }

    private fun formatDateTime(date: Date): String {
        return SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(date)
    }

    fun formatDateTime(timestamp: Long): String {
        return formatDateTime(Date(timestamp))
    }
}