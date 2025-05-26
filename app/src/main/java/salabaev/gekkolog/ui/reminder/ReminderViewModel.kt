package salabaev.gekkolog.ui.reminder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import salabaev.gekkolog.data.gecko.GeckoRepository
import salabaev.gekkolog.data.reminder.Reminder
import salabaev.gekkolog.data.reminder.ReminderRepository

class ReminderViewModel(
    private val repository: ReminderRepository,
    val geckoRepository: GeckoRepository
) : ViewModel() {

    private val _reminder = MutableLiveData<Reminder?>()
    val reminder: LiveData<Reminder?> = _reminder

    fun loadReminder(id: Int) {
        viewModelScope.launch {
            repository.getReminder(id).observeForever {
                _reminder.postValue(it)
            }
        }
    }

    fun saveReminder(reminder: Reminder) {
        viewModelScope.launch {
            if (reminder.id == 0) {
                repository.addReminder(reminder)
            } else {
                repository.updateReminder(reminder)
            }
        }
    }

    fun deleteReminder(id: Int) {
        viewModelScope.launch {
            if (id != 0) {
                repository.deleteReminder(id)
            }
        }
    }
}