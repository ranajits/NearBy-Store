package com.ranajit.nearbystore.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ranajit.nearbystore.model.Favorite
import com.ranajit.nearbystore.model.ProductDetails

@Dao
interface ProductDao {
    @Insert
    fun insertOfflineData(productDetails: ProductDetails?)

    @Insert
    fun insertFavorite(favorite: Favorite?)

    @get:Query("select * from productDetails")
    val allOfflineData: List<ProductDetails?>?

    @get:Query("select * from productDetails order by id desc limit 1")
    val lastInsertedRecord: List<ProductDetails?>?

    @get:Query("select * from favorite")
    val allFavoriteData: List<Favorite?>?

    @Query("DELETE FROM favorite WHERE combinations = :combi")
    fun deleteFavorite(combi: String?)
}