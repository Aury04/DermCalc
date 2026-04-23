package com.example.dermcalc

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dermcalc.navBarControl.NavManager
import com.example.dermcalc.data.DermCalcDatabase
import com.example.dermcalc.data.EasiScore
import com.example.dermcalc.data.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Activity per il calcolo dell'EASI (Eczema Area and Severity Index).
 * A differenza di altri calcolatori, questa Activity implementa un processo
 * a tappe per valutare i 4 distretti corporei (Testa, Arti Sup., Tronco, Arti Inf.).
 */
class EasiActivity : AppCompatActivity() {

    private val distretti = listOf("TESTA", "ARTI SUPERIORI", "TRONCO", "ARTI INFERIORI")
    private var indiceCorrente = 0
    private var easiTotale = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_easi)

        NavManager.inizializzaNavbar(this)

        val db = DermCalcDatabase.getDatabase(this)

        val rgEta = findViewById<RadioGroup>(R.id.rg_eta)
        val tvDistretto = findViewById<TextView>(R.id.tv_distretto_nome)
        val btnNext = findViewById<Button>(R.id.btn_next_easi)

        val groups = listOf(
            findViewById<RadioGroup>(R.id.rg_e),
            findViewById<RadioGroup>(R.id.rg_p),
            findViewById<RadioGroup>(R.id.rg_ex),
            findViewById<RadioGroup>(R.id.rg_l),
            findViewById<RadioGroup>(R.id.rg_area_easi)
        )

        btnNext.setOnClickListener {
            /**
             * --- VALIDAZIONE ---
             * Verifica che ogni parametro clinico sia stato selezionato prima di procedere
             */
            if (groups.any { it.checkedRadioButtonId == -1 }) {
                Toast.makeText(this, "Compila tutti i campi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            rgEta.getChildAt(0).isEnabled = false
            rgEta.getChildAt(1).isEnabled = false

            val isAdulto = findViewById<RadioButton>(R.id.rb_adulto).isChecked
            val pesoArea = getPesoEasi(indiceCorrente, isAdulto)

            // --- ESTRAZIONE VALORI ---
            val e = findViewById<RadioButton>(groups[0].checkedRadioButtonId).text.toString().toInt()
            val p = findViewById<RadioButton>(groups[1].checkedRadioButtonId).text.toString().toInt()
            val ex = findViewById<RadioButton>(groups[2].checkedRadioButtonId).text.toString().toInt()
            val l = findViewById<RadioButton>(groups[3].checkedRadioButtonId).text.toString().toInt()
            val areaScore = findViewById<RadioButton>(groups[4].checkedRadioButtonId).text.toString().toInt()

            /**
             * --- FORMULA EASI PARZIALE ---
             * La formula per ogni distretto è: (Eritema + Edema + Escoriazione + Lichenificazione) * PunteggioArea * PesoDistretto
             * Il valore viene sommato all'accumulatore globale.
             */
            easiTotale += (e + p + ex + l) * areaScore * pesoArea

            indiceCorrente++

            if (indiceCorrente < distretti.size) {
                // --- AGGIORNAMENTO UI PER PROSSIMO STEP ---
                tvDistretto.text = "Distretto: ${distretti[indiceCorrente]}"
                groups.forEach { it.clearCheck() }
                if (indiceCorrente == 3) btnNext.text = "CALCOLA RISULTATO"
            } else {
                // --- FINE DEI DISTRETTI: LOGICA DI SALVATAGGIO ---
                val cfAttivo = SessionManager.getUtenteCF(this)

                if (cfAttivo != null) {
                    val nuovoRecord = EasiScore(
                        utenteId = cfAttivo,
                        risultato = easiTotale,
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
                            db.dermCalcDao().insertEasi(nuovoRecord)
                        } catch (e: Exception) {
                        }
                    }
                }

                /**
                 *  --- NAVIGAZIONE AI RISULTATI ---
                 * Passiamo il valore calcolato e il tipo di calcolo alla ResultActivity
                 */
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("EXTRA_SCORE", easiTotale)
                intent.putExtra("EXTRA_TYPE", "EASI")
                startActivity(intent)
                finish()
            }
        }
    }

    /**
     * Restituisce il coefficiente moltiplicatore del distretto.
     * I pesi cambiano tra bambini (under 8 anni) e adulti poiché le proporzioni
     * corporee (specialmente la testa) variano drasticamente.
     */
    private fun getPesoEasi(distrettoIdx: Int, adulto: Boolean): Double {
        return if (adulto) {
            when (distrettoIdx) {
                0 -> 0.1; 1 -> 0.2; 2 -> 0.3; else -> 0.4
            }
        } else {
            when (distrettoIdx) {
                0 -> 0.2; 1 -> 0.2; 2 -> 0.3; else -> 0.3
            }
        }
    }
}