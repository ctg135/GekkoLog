package salabaev.gekkolog.ui.journal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import salabaev.gekkolog.data.event.Event
import salabaev.gekkolog.data.event.EventRepository
import salabaev.gekkolog.data.gecko.Gecko
import salabaev.gekkolog.data.gecko.GeckoRepository
import salabaev.gekkolog.data.reminder.Reminder
import salabaev.gekkolog.data.reminder.ReminderRepository

class JournalViewModel(
    private val eventRepository: EventRepository,
    private val reminderRepository: ReminderRepository,
    private val geckoRepository: GeckoRepository
) : ViewModel() {

    private val _todayNotifications = MutableLiveData<List<Reminder>>()
    val todayNotifications: LiveData<List<Reminder>> = _todayNotifications

    private val _todayEvents = MutableLiveData<List<Event>>()
    val todayEvents: LiveData<List<Event>> = _todayEvents

    fun loadTodayNotifications(startOfDay: Long, endOfDay: Long) {
        viewModelScope.launch {
            reminderRepository.getReminders(startOfDay, endOfDay).observeForever {
                _todayNotifications.postValue(it)
            }
        }

    }

    fun loadTodayEvents(startOfDay: Long, endOfDay: Long) {
        viewModelScope.launch {
            eventRepository.getEvents(startOfDay, endOfDay).observeForever {
                _todayEvents.postValue(it)
            }
        }
    }

    fun getGeckos(lifecycleOwner: androidx.lifecycle.LifecycleOwner, callback: (Map<Int, Gecko>) -> Unit) {
        geckoRepository.geckoList?.observe(lifecycleOwner) { geckos: List<Gecko> ->
            callback(geckos.associateBy { it.id })
        }
    }
}