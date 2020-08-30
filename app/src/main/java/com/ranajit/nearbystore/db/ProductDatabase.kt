package com.ranajit.nearbystore.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ranajit.nearbystore.model.Favorite
import com.ranajit.nearbystore.model.ProductDetails

@Database(entities = [ProductDetails::class, Favorite::class], version = 1, exportSchema = false)
abstract class ProductDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao?
    fun cleanUp() {
        productDatabase = null
    }

    companion object {
        private const val TAG = "ProductDatabase"
        private var productDatabase: ProductDatabase? = null

        // synchronized is use to avoid concurrent access in multithred environment
        @Synchronized
        fun getInstance(context: Context): ProductDatabase? {
            if (null == productDatabase) {
                productDatabase = buildDatabaseInstance(context)
            }
            return productDatabase
        }

        private fun buildDatabaseInstance(context: Context): ProductDatabase {
            return Room.databaseBuilder(
                context, ProductDatabase::class.java, "productDatabase"
            ).allowMainThreadQueries()
                .build()
        }
    }
}