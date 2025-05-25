package salabaev.gekkolog.data.event

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventRepository(private val dao: EventDao) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun getEvent(eventId: Int): LiveData<Event> {
        return dao.getEvent(eventId)
    }

    fun addEvent(event: Event){
        coroutineScope.launch (Dispatchers.IO) {
            dao.addEvent(event)
        }
    }

    fun updateEvent(event: Event){
        coroutineScope.launch (Dispatchers.IO) {
            dao.updateEvent(event)
        }
    }

    fun deleteEvent(event: Event) {
        coroutineScope.launch (Dispatchers.IO) {
            dao.deleteEvent(event)
        }
    }

    fun deleteEvent(eventId: Int) {
        coroutineScope.launch(Dispatchers.IO) {
            dao.deleteEvent(eventId)
        }
    }

}