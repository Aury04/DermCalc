package com.example.dermcalc

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dermcalc.navBarControl.NavManager
import com.example.dermcalc.data.DermCalcDatabase
import com.example.dermcalc.data.PasiScore
import com.example.dermcalc.data.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Activity per il calcolo del PASI (Psoriasis Area and Severity Index).
 * Rappresenta lo standard internazionale per la valutazione della gravità
 * della psoriasi a placche, combinando estensione dell'area e severità clinica.
 */
class PasiActivity : AppCompatActivity() {

    private val distretti = listOf("TESTA", "ARTI SUPERIORI", "TRONCO", "ARTI INFERIORI")
    private val pesi = listOf(0.1, 0.2, 0.3, 0.4)
    private var indiceCorrente = 0
    private var pasiTotale = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pasi)

        NavManager.inizializzaNavbar(this)

        val db = DermCalcDatabase.getDatabase(this)

        val tvTitolo = findViewById<TextView>(R.id.tv_distretto_titolo)
        val btnNext = findViewById<Button>(R.id.btn_next)

        val rgE = findViewById<RadioGroup>(R.id.rg_eritema)
        val rgI = findViewById<RadioGroup>(R.id.rg_indurimento)
        val rgD = findViewById<RadioGroup>(R.id.rg_desquamazione)
        val rgA = findViewById<RadioGroup>(R.id.rg_area)

        btnNext.setOnClickListener {
            /**
             * --- VALIDAZIONE ---
             * Assicura che l'utente non lasci parametri vuoti per il distretto corrente
             */
            if (rgE.checkedRadioButtonId == -1 || rgI.checkedRadioButtonId == -1 ||
                rgD.checkedRadioButtonId == -1 || rgA.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Seleziona tutti i parametri!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            /**
             * --- RECUPERO DATI ---
             * Conversione del testo del RadioButton (0-4) in valore intero per il calcolo
             */
            val e = findViewById<RadioButton>(rgE.checkedRadioButtonId).text.toString().toInt()
            val i = findViewById<RadioButton>(rgI.checkedRadioButtonId).text.toString().toInt()
            val d = findViewById<RadioButton>(rgD.checkedRadioButtonId).text.toString().toInt()
            val a = findViewById<RadioButton>(rgA.checkedRadioButtonId).text.toString().toInt()

            /**
             * --- LOGICA DI CALCOLO (FORMULA PASI) ---
             * Per ogni distretto: (Eritema + Indurimento + Desquamazione) * Area * PesoDistretto.
             * Il punteggio totale è la sommatoria dei 4 parziali.
             */
            val parziale = (e + i + d) * a * pesi[indiceCorrente]
            pasiTotale += parziale

            indiceCorrente++

            if (indiceCorrente < distretti.size) {
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

                    /**
                     * Salvataggio in Background:
                     * lifecycleScope.launch(Dispatchers.IO) per eseguire la query
                     * su un thread dedicato all'I/O. Questo evita il blocco dell'interfaccia
                     * utente (ANR - App Not Responding) durante la scrittura su disco.
                     */
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            db.dermCalcDao().insertPasi(nuovoRecord)
                        } catch (e: Exception) {
                        }
                    }
                }

                /**
                 * --- NAVIGAZIONE AI RISULTATI ---
                 * Passiamo il valore calcolato e il tipo di calcolo alla ResultActivity
                 */
                val intent = Intent(this@PasiActivity, ResultActivity::class.java)
                intent.putExtra("EXTRA_SCORE", pasiTotale)
                intent.putExtra("EXTRA_TYPE", "PASI")
                startActivity(intent)
                finish()
            }
        }
    }
}