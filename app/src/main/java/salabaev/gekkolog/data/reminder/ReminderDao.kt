package salabaev.gekkolog.data.reminder

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE id = :id")
    fun getReminder(id: Int): LiveData<Reminder>

    @Insert
    fun addReminder(reminder: Reminder)

    @Update
    fun updateReminder(reminder: Reminder)

    @Query("DELETE FROM reminders WHERE id = :id")
    fun deleteReminder(id: Int)
}