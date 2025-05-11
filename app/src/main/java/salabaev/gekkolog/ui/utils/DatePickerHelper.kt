package salabaev.gekkolog.ui.utils

import android.app.DatePickerDialog
import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DatePickerHelper {

    fun showDatePickerDialog(
        context: Context,
        initialDate: Long? = null,
        onDateSelected: (date: Long, formattedDate: String) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        initialDate?.let { calendar.timeInMillis = it }

        DatePickerDialog(
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
        ).show()
    }

    private fun formatDate(date: Date): String {
        return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)
    }

    fun formatDate(timestamp: Long): String {
        return formatDate(Date(timestamp))
    }
}