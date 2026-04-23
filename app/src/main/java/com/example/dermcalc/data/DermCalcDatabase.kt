package com.example.dermcalc.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Punto di accesso principale al database locale dell'app.
 * Definisce le entità (tabelle) e la versione dello schema.
 * * Entità incluse:
 * - [Utente]: Dati anagrafici e credenziali.
 * - [PasiScore], [EasiScore], [BmiScore], [BsaScore]: Storico dei vari calcoli clinici.
 */
@Database(entities = [Utente::class, PasiScore::class, EasiScore::class, BmiScore::class, BsaScore::class], version = 2)
abstract class DermCalcDatabase : RoomDatabase() {

    /**
     * Fornisce l'accesso ai metodi definiti nel DAO.
     */
    abstract fun dermCalcDao(): DermCalcDao

    companion object {
        /**
         * INSTANCE manterrà il riferimento al database una volta aperto.
         * L'annotazione @Volatile garantisce che il valore sia sempre aggiornato
         * tra i vari thread di esecuzione, evitando inconsistenze.
         */
        @Volatile
        private var INSTANCE: DermCalcDatabase? = null

        /**
         * Metodo per ottenere l'istanza del database.
         * Implementa il pattern Singleton con "Double-Checked Locking" per assicurarsi
         * che venga creata una sola istanza del database durante tutto il ciclo di vita dell'app.
         *
         * @param context Il contesto dell'applicazione per inizializzare il builder.
         * @return L'istanza unica di [DermCalcDatabase].
         */
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