package com.notsatria.reboot.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.notsatria.reboot.data.local.dao.RelapseDao
import com.notsatria.reboot.data.local.entity.RelapseEventEntity
import kotlin.jvm.java

@Database(entities = [RelapseEventEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun relapseDao(): RelapseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stop_pmo.db"
                )
                    .build()
            }
        }
    }
}