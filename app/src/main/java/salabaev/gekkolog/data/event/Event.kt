package salabaev.gekkolog.data.event

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import salabaev.gekkolog.data.gecko.Gecko

@Entity(tableName = "events",
    foreignKeys = [
        ForeignKey(
            entity = Gecko::class,
            parentColumns = ["id"],
            childColumns = ["geckoId"],
            onDelete = ForeignKey.CASCADE
        )
    ])
class Event {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var date: Long? = null
    var geckoId: Int? = null
    var type: String? = null
    var description: String? = null
    var weight: Float? = null
    var photoPath: String? = null
    var shedSuccess: Boolean? = null
    var feedType: String? = null
    var feedSucces: Boolean? = null
}
