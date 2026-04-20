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
import com.example.dermcalc.data.DermCalcDatabase
import com.example.dermcalc.data.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BsaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bsa)

        val db = DermCalcDatabase.getDatabase(this)

        val etAltezza = findViewById<EditText>(R.id.et_altezza_bsa)
        val etPeso = findViewById<EditText>(R.id.et_peso_bsa)
        val btnCalcola = findViewById<Button>(R.id.btn_calcola_bsa)

        btnCalcola.setOnClickListener {
            val altezzaStr = etAltezza.text.toString()
            val pesoStr = etPeso.text.toString()

            // 1. Validazione input
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

            // 2. Calcolo BSA
            val bsa = sqrt((altezza * peso) / 3600)

            // 3. LOGICA DI SALVATAGGIO (Solo se l'utente è loggato)
            val cfAttivo = SessionManager.getUtenteCF(this)

            if (cfAttivo != null) {
                val nuovoRecord = BsaScore(
                    utenteId = cfAttivo,
                    risultato = bsa,
                    dataCalcolo = SessionManager.getDataCorrente()
                )

                // Lanciamo il salvataggio in background senza bloccare la UI
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        db.dermCalcDao().insertBsa(nuovoRecord)
                    } catch (e: Exception) {
                        // Errore nel salvataggio, ma l'utente vedrà comunque il risultato
                    }
                }
            }

            // 4. NAVIGAZIONE (Avviene SEMPRE, istantaneamente)
            val intent = Intent(this@BsaActivity, ResultActivity::class.java)
            intent.putExtra("EXTRA_SCORE", bsa)
            intent.putExtra("EXTRA_TYPE", "BSA")
            startActivity(intent)
        }
    }
}