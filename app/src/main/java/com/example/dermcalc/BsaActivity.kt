package com.example.dermcalc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dermcalc.data.BsaScore
import kotlin.math.sqrt
import androidx.lifecycle.lifecycleScope
import com.example.dermcalc.navBarControl.NavManager
import com.example.dermcalc.data.DermCalcDatabase
import com.example.dermcalc.data.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Activity per il calcolo della Superficie Corporea (BSA - Body Surface Area).
 * Utilizza la formula di Mosteller, lo standard più comune nella pratica clinica
 * dermatologica per il calcolo dei dosaggi farmacologici.
 */
class BsaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bsa)

        NavManager.inizializzaNavbar(this)

        val db = DermCalcDatabase.getDatabase(this)

        val etAltezza = findViewById<EditText>(R.id.et_altezza_bsa)
        val etPeso = findViewById<EditText>(R.id.et_peso_bsa)
        val btnCalcola = findViewById<Button>(R.id.btn_calcola_bsa)

        btnCalcola.setOnClickListener {
            val altezzaStr = etAltezza.text.toString()
            val pesoStr = etPeso.text.toString()

            /**
             *--- VALIDAZIONE INPUT ---
             * Verifica che i campi non siano vuoti e che i valori siano positivi.
             */
            if (altezzaStr.isEmpty() || pesoStr.isEmpty()) {
                Toast.makeText(this, "Per favore, inserisci tutti i dati", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val altezza = altezzaStr.toDouble()
            val peso = pesoStr.toDouble()

            if (altezza <= 0 || peso <= 0) {
                Toast.makeText(this, "Dati non validi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            /**
             *  --- CALCOLO BSA ---
             * La formula applicata è: √( (Altezza(cm) * Peso(kg)) / 3600 )
             * Il risultato rappresenta i metri quadrati (m²) della superficie corporea.
             */
            val bsa = sqrt((altezza * peso) / 3600)

            /**
             * --- LOGICA DI SALVATAGGIO ---
             * Solo se l'utente è loggato
             */
            val cfAttivo = SessionManager.getUtenteCF(this)

            if (cfAttivo != null) {
                val nuovoRecord = BsaScore(
                    utenteId = cfAttivo,
                    risultato = bsa,
                    dataCalcolo = SessionManager.getDataCorrente()
                )

                /**
                 * Salvataggio in Background:
                 * lifecycleScope.launch(Dispatchers.IO) per eseguire la query
                 * su un thread dedicato all'I/O. Questo evita il blocco dell'interfaccia
                 * utente (ANR - App Not Responding) durante la scrittura su disco.
                 */
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        db.dermCalcDao().insertBsa(nuovoRecord)
                    } catch (e: Exception) {
                    }
                }
            }

            /**
             *  --- NAVIGAZIONE AI RISULTATI ---
             * Passiamo il valore calcolato e il tipo di calcolo alla ResultActivity
             */
            val intent = Intent(this@BsaActivity, ResultActivity::class.java)
            intent.putExtra("EXTRA_SCORE", bsa)
            intent.putExtra("EXTRA_TYPE", "BSA")
            startActivity(intent)
        }
    }
}