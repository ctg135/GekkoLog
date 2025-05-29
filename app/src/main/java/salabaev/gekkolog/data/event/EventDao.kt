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

    @Query("SELECT * FROM events WHERE geckoId = :geckoId AND type = 'FEED' ORDER BY date DESC LIMIT 4")
    fun get4LastFeeds(geckoId: Int): LiveData<List<Event>>
}