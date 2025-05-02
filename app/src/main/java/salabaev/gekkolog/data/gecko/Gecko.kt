package salabaev.gekkolog.data.gecko

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="geckos")
class Gecko() {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var name: String? = null
    var morph: String? = null
    var gender: String? = null
    var feedPeriod: Int? = null
    var photoPath: String? = null

}