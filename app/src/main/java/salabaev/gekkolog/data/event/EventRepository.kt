package salabaev.gekkolog.data.event

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    suspend fun get3LastFeedsSync(geckoId: Int): List<Event> {
        return withContext(Dispatchers.IO) {
            // Реализация синхронного получения событий
            return@withContext dao.getLast3FeedsSync(geckoId)
        }
    }

    fun getLastFeed(geckoId: Int): Event? {
        return dao.getLastFeed(geckoId)
    }

    fun getLastWeight(geckoId: Int): Event? {
        return dao.getLastWeight(geckoId)
    }

    fun getLastShed(geckoId: Int): Event? {
        return dao.getLastShed(geckoId)
    }

    fun getEvents(since: Long, until: Long): LiveData<List<Event>> {
        return dao.getEvents(since, until)
    }
}