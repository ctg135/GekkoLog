package salabaev.gekkolog.data.event

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface EventDao {
    @Query("SELECT * FROM events WHERE id = :id")
    fun getEvent(id: Int): LiveData<Event>

    @Insert
    fun addEvent(event: Event)

    @Update
    fun updateEvent(event: Event)

    @Delete
    fun deleteEvent(event: Event)

    @Query("DELETE FROM events WHERE id = :id")
    fun deleteEvent(id: Int)

    @Query("SELECT * FROM events WHERE geckoId = :geckoId AND type = 'FEED' ORDER BY date DESC LIMIT 3")
    fun getLast3FeedsSync(geckoId: Int): List<Event>

    @Query("SELECT * FROM events WHERE geckoId = :geckoId AND type = 'FEED' ORDER BY date DESC LIMIT 1")
    fun getLastFeed(geckoId: Int): Event?

    @Query("SELECT * FROM events WHERE geckoId = :geckoId AND type = 'WEIGHT' ORDER BY date DESC LIMIT 1")
    fun getLastWeight(geckoId: Int): Event?

    @Query("SELECT * FROM events WHERE geckoId = :geckoId AND type = 'SHED' ORDER BY date DESC LIMIT 1")
    fun getLastShed(geckoId: Int): Event?

    @Query("SELECT * FROM events WHERE date BETWEEN :since AND :until ")
    fun getEvents(since: Long, until: Long): LiveData<List<Event>>
}