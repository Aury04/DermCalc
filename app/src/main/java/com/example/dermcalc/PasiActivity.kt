package com.example.dermcalc

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dermcalc.NavBarControl.NavManager
import com.example.dermcalc.data.DermCalcDatabase
import com.example.dermcalc.data.PasiScore
import com.example.dermcalc.data.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PasiActivity : AppCompatActivity() {

    private val distretti = listOf("TESTA", "ARTI SUPERIORI", "TRONCO", "ARTI INFERIORI")
    private val pesi = listOf(0.1, 0.2, 0.3, 0.4)
    private var indiceCorrente = 0
    private var pasiTotale = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pasi)

        NavManager.inizializzaNavbar(this)

        // Inizializzazione Database
        val db = DermCalcDatabase.getDatabase(this)

        val tvTitolo = findViewById<TextView>(R.id.tv_distretto_titolo)
        val btnNext = findViewById<Button>(R.id.btn_next)

        val rgE = findViewById<RadioGroup>(R.id.rg_eritema)
        val rgI = findViewById<RadioGroup>(R.id.rg_indurimento)
        val rgD = findViewById<RadioGroup>(R.id.rg_desquamazione)
        val rgA = findViewById<RadioGroup>(R.id.rg_area)

        btnNext.setOnClickListener {
            // Validazione: controlla se tutti i campi sono selezionati
            if (rgE.checkedRadioButtonId == -1 || rgI.checkedRadioButtonId == -1 ||
                rgD.checkedRadioButtonId == -1 || rgA.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Seleziona tutti i parametri!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Recupero valori (usa il testo del RadioButton come valore numerico)
            val e = findViewById<RadioButton>(rgE.checkedRadioButtonId).text.toString().toInt()
            val i = findViewById<RadioButton>(rgI.checkedRadioButtonId).text.toString().toInt()
            val d = findViewById<RadioButton>(rgD.checkedRadioButtonId).text.toString().toInt()
            val a = findViewById<RadioButton>(rgA.checkedRadioButtonId).text.toString().toInt()

            // Calcolo parziale per il distretto
            val parziale = (e + i + d) * a * pesi[indiceCorrente]
            pasiTotale += parziale

            indiceCorrente++

            if (indiceCorrente < distretti.size) {
                // Passa al prossimo distretto
                tvTitolo.text = "Distretto: ${distretti[indiceCorrente]}"
                rgE.clearCheck(); rgI.clearCheck(); rgD.clearCheck(); rgA.clearCheck()
                if (indiceCorrente == distretti.size - 1) btnNext.text = "CALCOLA RISULTATO"
            } else {
                // --- FINE DEI DISTRETTI: LOGICA DI SALVATAGGIO ---
                val cfAttivo = SessionManager.getUtenteCF(this)

                if (cfAttivo != null) {
                    val nuovoRecord = PasiScore(
                        utenteId = cfAttivo,
                        risultato = pasiTotale,
                        dataCalcolo = SessionManager.getDataCorrente()
                    )

                    // Salvataggio in background
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            db.dermCalcDao().insertPasi(nuovoRecord)
                        } catch (e: Exception) {
                            // Errore DB gestito silenziosamente
                        }
                    }
                }

                // --- NAVIGAZIONE AI RISULTATI (Sempre eseguita) ---
                val intent = Intent(this@PasiActivity, ResultActivity::class.java)
                intent.putExtra("EXTRA_SCORE", pasiTotale)
                intent.putExtra("EXTRA_TYPE", "PASI")
                startActivity(intent)
                finish() // Chiude l'activity di calcolo
            }
        }
    }
}