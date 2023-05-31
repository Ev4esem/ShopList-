package com.example.packlist.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RenameColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import com.example.packlist.entities.*

@Database(
    entities = [
        LibraryItem::class,
        NoteItem::class,
        ShopListItem::class,
        ShopListNameItem::class,], version = 3,
    exportSchema = true, autoMigrations = [
        AutoMigration(from = 2 , to = 3 , spec = MainDataBase.SpecMigration::class)

     ]
    )
abstract class MainDataBase : RoomDatabase() {
    @DeleteColumn(tableName = "library", columnName = "price")
    class SpecMigration : AutoMigrationSpec

    abstract fun getDao(): Dao

    companion object {
        @Volatile
        private var INSTANCE: MainDataBase? = null
        fun getDataBase(context: Context): MainDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDataBase::class.java,
                    "shopping_list.db"
                ).build()
                instance
            }

        }
    }
}