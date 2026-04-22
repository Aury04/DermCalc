package com.example.dermcalc.data

import androidx.room.*

/**
 * Interfaccia DAO (Data Access Object) per l'accesso ai dati tramite Room.
 * Contiene i metodi per interagire con le tabelle degli utenti e dei vari calcoli clinici.
 */
@Dao
interface DermCalcDao {

    /**
     * Verifica la presenza di un utente nel database tramite il Codice Fiscale.
     * Utilizzato durante la registrazione per evitare duplicati.
     * @param cf Il codice fiscale da cercare.
     * @return L'oggetto [Utente] se trovato, null altrimenti.
     */
    @Query("SELECT * FROM utenti WHERE codFiscale = :cf LIMIT 1")
    suspend fun checkUtenteEsistente(cf: String): Utente?

    /**
     * Registra un nuovo utente.
     * @param utente L'entità utente da persistere.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUtente(utente: Utente): Unit

    /**
     * Esegue la verifica delle credenziali per l'accesso.
     * @param cf Codice Fiscale inserito (username).
     * @param pass Hash della password inserita.
     * @return L'utente autenticato se le credenziali sono corrette, null altrimenti.
     */
    @Query("SELECT * FROM utenti WHERE codFiscale = :cf AND password = :pass")
    suspend fun login(cf: String, pass: String): Utente?

    /**
     * Recupera il profilo completo di un utente specifico.
     */
    @Query("SELECT * FROM utenti WHERE codFiscale = :cf")
    suspend fun getUtente(cf: String): Utente?

    /**
     * Salva un nuovo risultato del calcolo PASI.
     */
    @Insert
    suspend fun insertPasi(score: PasiScore): Unit

    /**
     * Recupera tutti i calcoli PASI salvati da un utente, dal più recente al più datato.
     */
    @Query("SELECT * FROM pasi_scores WHERE utenteId = :cf ORDER BY dataCalcolo DESC")
    suspend fun getPasiByUser(cf: String): List<PasiScore>

    /**
     * Salva un nuovo risultato del calcolo EASI.
     */
    @Insert
    suspend fun insertEasi(score: EasiScore): Unit

    /**
     * Recupera lo storico dei calcoli EASI per l'utente specificato.
     */
    @Query("SELECT * FROM easi_scores WHERE utenteId = :cf ORDER BY dataCalcolo DESC")
    suspend fun getEasiByUser(cf: String): List<EasiScore>

    /**
     * Salva un nuovo risultato del calcolo BMI.
     */
    @Insert
    suspend fun insertBmi(score: BmiScore): Unit

    /**
     * Recupera lo storico dei calcoli BMI per l'utente specificato.
     */
    @Query("SELECT * FROM bmi_scores WHERE utenteId = :cf ORDER BY dataCalcolo DESC")
    suspend fun getBmiByUser(cf: String): List<BmiScore>

    /**
     * Salva un nuovo risultato del calcolo BSA.
     */
    @Insert
    suspend fun insertBsa(score: BsaScore): Unit

    /**
     * Recupera lo storico dei calcoli BSA per l'utente specificato.
     */
    @Query("SELECT * FROM bsa_scores WHERE utenteId = :cf ORDER BY dataCalcolo DESC")
    suspend fun getBsaByUser(cf: String): List<BsaScore>


}