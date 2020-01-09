package com.magnox.birthdays.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [PersonEntity::class, GroupEntity::class], version = 1)
@TypeConverters(CalendarTypeConverter::class)
abstract class PersonDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDao

    companion object{

        @Volatile private var INSTANCE: PersonDatabase? = null

        fun getInstance(context: Context): PersonDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): PersonDatabase {
            val fileName = "Birthdays.db"

            return Room
                .databaseBuilder(context.applicationContext, PersonDatabase::class.java, fileName)
//                .createFromFile(file)
                .build()
        }
    }
}