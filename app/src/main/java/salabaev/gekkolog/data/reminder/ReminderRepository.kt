package salabaev.gekkolog.data.reminder

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderRepository(private val dao: ReminderDao) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun getReminder(reminderId: Int): LiveData<Reminder> {
        return dao.getReminder(reminderId)
    }

    fun addReminder(reminder: Reminder){
        coroutineScope.launch (Dispatchers.IO) {
            dao.addReminder(reminder)
        }
    }

    fun updateReminder(reminder: Reminder){
        coroutineScope.launch (Dispatchers.IO) {
            dao.updateReminder(reminder)
        }
    }

    fun deleteReminder(reminderId: Int) {
        coroutineScope.launch(Dispatchers.IO) {
            dao.deleteReminder(reminderId)
        }
    }

    fun deleteAutoRemainders(geckoId: Int, type: String) {
        coroutineScope.launch(Dispatchers.IO) {
            dao.deleteAutoRemainders(geckoId, type)
        }
    }

    fun deleteBetweenDates(geckoId: Int, since: Long, until: Long, type: String) {
        if (since <= until) return
        coroutineScope.launch(Dispatchers.IO) {
            dao.deleteBetweenDates(geckoId, since, until, type)
        }
    }
}