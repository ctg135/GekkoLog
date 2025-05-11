package salabaev.gekkolog.data.gecko

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="geckos")
class Gecko() {
    @PrimaryKey(autoGenerate = true)
    public var id: Int = 0
    public var name: String? = null
    public var morph: String? = null
    public var gender: String? = null
    public var feedPeriod: Int? = null
    public var photoPath: String? = null
    public var birthDate: Long? = null
}