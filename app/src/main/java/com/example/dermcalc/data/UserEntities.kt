package com.example.dermcalc.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "utenti")
data class Utente(
    @PrimaryKey val codFiscale: String,
    val nome: String,
    val cognome: String,
    val email: String,
    val password: String
)

// Creiamo una classe base per evitare di ripetere il codice
open class BaseScore(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val utenteId: String,
    val risultato: Double,
    val dataCalcolo: String
)

@Entity(tableName = "pasi_scores", foreignKeys = [ForeignKey(entity = Utente::class, parentColumns = ["codFiscale"], childColumns = ["utenteId"], onDelete = ForeignKey.CASCADE)])
class PasiScore(id: Int = 0, utenteId: String, risultato: Double, dataCalcolo: String) : BaseScore(id, utenteId, risultato, dataCalcolo)

@Entity(tableName = "easi_scores", foreignKeys = [ForeignKey(entity = Utente::class, parentColumns = ["codFiscale"], childColumns = ["utenteId"], onDelete = ForeignKey.CASCADE)])
class EasiScore(id: Int = 0, utenteId: String, risultato: Double, dataCalcolo: String) : BaseScore(id, utenteId, risultato, dataCalcolo)

@Entity(tableName = "bmi_scores", foreignKeys = [ForeignKey(entity = Utente::class, parentColumns = ["codFiscale"], childColumns = ["utenteId"], onDelete = ForeignKey.CASCADE)])
class BmiScore(id: Int = 0, utenteId: String, risultato: Double, dataCalcolo: String) : BaseScore(id, utenteId, risultato, dataCalcolo)

@Entity(tableName = "bsa_scores", foreignKeys = [ForeignKey(entity = Utente::class, parentColumns = ["codFiscale"], childColumns = ["utenteId"], onDelete = ForeignKey.CASCADE)])
class BsaScore(id: Int = 0, utenteId: String, risultato: Double, dataCalcolo: String) : BaseScore(id, utenteId, risultato, dataCalcolo)