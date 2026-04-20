package com.example.dermcalc.data

import androidx.room.*

@Dao
interface DermCalcDao {
    // Aggiungiamo ": Long" a tutti gli insert per evitare il bug "signature V"

    @Query("SELECT * FROM utenti WHERE codFiscale = :cf LIMIT 1")
    suspend fun checkUtenteEsistente(cf: String): Utente?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUtente(utente: Utente): Unit

    @Query("SELECT * FROM utenti WHERE codFiscale = :cf AND password = :pass")
    suspend fun login(cf: String, pass: String): Utente?

    @Insert
    suspend fun insertPasi(score: PasiScore): Unit

    @Query("SELECT * FROM pasi_scores WHERE utenteId = :cf ORDER BY dataCalcolo DESC")
    suspend fun getPasiByUser(cf: String): List<PasiScore>

    @Insert
    suspend fun insertEasi(score: EasiScore): Unit

    @Query("SELECT * FROM easi_scores WHERE utenteId = :cf ORDER BY dataCalcolo DESC")
    suspend fun getEasiByUser(cf: String): List<EasiScore>

    @Insert
    suspend fun insertBmi(score: BmiScore): Unit

    @Query("SELECT * FROM bmi_scores WHERE utenteId = :cf ORDER BY dataCalcolo DESC")
    suspend fun getBmiByUser(cf: String): List<BmiScore>

    @Insert
    suspend fun insertBsa(score: BsaScore): Unit

    // Attenzione: qui avevi un errore nel nome della tabella (pasi_scores invece di bsa_scores)
    @Query("SELECT * FROM bsa_scores WHERE utenteId = :cf ORDER BY dataCalcolo DESC")
    suspend fun getBsaByUser(cf: String): List<BsaScore>

    // Opzionale: aggiungi questa per recuperare i dati dell'utente nel profilo
    @Query("SELECT * FROM utenti WHERE codFiscale = :cf")
    suspend fun getUtente(cf: String): Utente?
}