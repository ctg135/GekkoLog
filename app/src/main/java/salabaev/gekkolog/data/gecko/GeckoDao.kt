package salabaev.gekkolog.data.gecko

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GeckoDao {
    @Query("SELECT * FROM geckos")
    fun getGeckos(): LiveData<List<Gecko>>

    @Query("SELECT * FROM geckos WHERE id = :id")
    fun getGecko(id: Int): LiveData<Gecko>

    @Insert
    fun addGecko(gecko: Gecko)

    @Delete
    fun deleteGecko(gecko: Gecko)

    @Query("DELETE FROM geckos WHERE id = :id")
    fun deleteGecko(id: Int)

    @Update
    fun updateGecko(gecko: Gecko)
}