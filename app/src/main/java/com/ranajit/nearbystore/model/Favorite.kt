package com.ranajit.nearbystore.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite")
class Favorite {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "combinations")
    var combinations: String? = null
}