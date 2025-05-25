package salabaev.gekkolog.ui.event

import android.icu.text.PluralRules
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import salabaev.gekkolog.data.event.Event
import salabaev.gekkolog.data.event.EventRepository
import salabaev.gekkolog.data.gecko.GeckoRepository

class EventViewModel (private val repository: EventRepository,
                      private val geckoRepository: GeckoRepository) : ViewModel() {
    private val _event = MutableLiveData<Event?>()
    val event: LiveData<Event?> = _event

    fun loadEvent(eventId: Int) {
        viewModelScope.launch {
            repository.getEvent(eventId).observeForever { event ->
                _event.postValue(event)
            }
        }
    }

    fun saveEvent(event: Event) {
        viewModelScope.launch {
            if (event.id == 0) {
                repository.addEvent(event)
            } else {
                repository.updateEvent(event)
            }
        }
    }

    fun deleteEvent(eventId: Int) {
        viewModelScope.launch {
            if (eventId != 0) {
                repository.deleteEvent(eventId)
            }
        }
    }


}