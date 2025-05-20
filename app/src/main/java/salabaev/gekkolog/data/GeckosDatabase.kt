package salabaev.gekkolog.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import salabaev.gekkolog.data.event.Event
import salabaev.gekkolog.data.event.EventDao
import salabaev.gekkolog.data.gecko.Gecko
import salabaev.gekkolog.data.gecko.GeckoDao

@Database(
    entities = [Gecko::class, Event::class],
    version = 3,
    exportSchema = false
)
abstract class GeckosDatabase: RoomDatabase() {
    abstract fun GeckoDao(): GeckoDao
    abstract fun EventDao(): EventDao

    companion object {
        private var INSTANCE: GeckosDatabase? = null
        fun getInstance(context: Context): GeckosDatabase {

            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        GeckosDatabase::class.java,
                        "geckos"

                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}