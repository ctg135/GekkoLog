package salabaev.gekkolog.ui.utils

import android.app.DatePickerDialog
import android.content.Context
import java.util.Calendar

object DatePickerHelper {
    fun showDatePickerDialog(
        context: Context,
        onDateSelected: (String) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                val ageText = calculateAge(selectedDate)
                onDateSelected(ageText)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun calculateAge(birthDate: Calendar): String {
        val today = Calendar.getInstance()
        val years = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)
        val months = today.get(Calendar.MONTH) - birthDate.get(Calendar.MONTH)
        return "$years лет ${months} месяцев"
    }
}