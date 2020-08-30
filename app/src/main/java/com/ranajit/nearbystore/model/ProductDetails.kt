package com.ranajit.nearbystore.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productDetails")
class ProductDetails {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "productType")
    var productType: String? = null

    @ColumnInfo(name = "productUri")
    var productUri: String? = null
}