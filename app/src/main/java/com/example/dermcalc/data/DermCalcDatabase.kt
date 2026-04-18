package com.example.dermcalc.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Utente::class, PasiScore::class, EasiScore::class, BmiScore::class, BsaScore::class], version = 1)
abstract class DermCalcDatabase : RoomDatabase() {
    abstract fun dermCalcDao(): DermCalcDao

    companion object {
        @Volatile
        private var INSTANCE: DermCalcDatabase? = null

        fun getDatabase(context: Context): DermCalcDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DermCalcDatabase::class.java,
                    "dermcalc_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}