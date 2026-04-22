package com.example.dermcalc.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Rappresenta la tabella degli utenti registrati.
 * Il Codice Fiscale viene usato come chiave primaria univoca.
 */
@Entity(tableName = "utenti")
data class Utente(
    @PrimaryKey val codFiscale: String,
    val nome: String,
    val cognome: String,
    val email: String,
    val password: String
)

/**
 * Classe base che definisce i campi comuni a tutti i calcoli clinici.
 * @property id Identificativo auto-generato per ogni salvataggio.
 * @property utenteId Chiave esterna che collega il calcolo all'utente (CF).
 * @property risultato Il valore numerico ottenuto dal calcolo.
 * @property dataCalcolo Data in formato dd/MM/yyyy.
 */
open class BaseScore(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val utenteId: String,
    val risultato: Double,
    val dataCalcolo: String
)

/**
 * Entità per il salvataggio dei punteggi PASI (Psoriasis Area and Severity Index).
 */
@Entity(tableName = "pasi_scores", foreignKeys = [ForeignKey(entity = Utente::class, parentColumns = ["codFiscale"], childColumns = ["utenteId"], onDelete = ForeignKey.CASCADE)])
class PasiScore(id: Int = 0, utenteId: String, risultato: Double, dataCalcolo: String) : BaseScore(id, utenteId, risultato, dataCalcolo)

/**
 * Entità per il salvataggio dei punteggi EASI (Eczema Area and Severity Index).
 */
@Entity(tableName = "easi_scores", foreignKeys = [ForeignKey(entity = Utente::class, parentColumns = ["codFiscale"], childColumns = ["utenteId"], onDelete = ForeignKey.CASCADE)])
class EasiScore(id: Int = 0, utenteId: String, risultato: Double, dataCalcolo: String) : BaseScore(id, utenteId, risultato, dataCalcolo)

/**
 * Entità per il salvataggio dell'Indice di Massa Corporea (BMI).
 */
@Entity(tableName = "bmi_scores", foreignKeys = [ForeignKey(entity = Utente::class, parentColumns = ["codFiscale"], childColumns = ["utenteId"], onDelete = ForeignKey.CASCADE)])
class BmiScore(id: Int = 0, utenteId: String, risultato: Double, dataCalcolo: String) : BaseScore(id, utenteId, risultato, dataCalcolo)

/**
 * Entità per il salvataggio della Superficie Corporea (BSA).
 */
@Entity(tableName = "bsa_scores", foreignKeys = [ForeignKey(entity = Utente::class, parentColumns = ["codFiscale"], childColumns = ["utenteId"], onDelete = ForeignKey.CASCADE)])
class BsaScore(id: Int = 0, utenteId: String, risultato: Double, dataCalcolo: String) : BaseScore(id, utenteId, risultato, dataCalcolo)