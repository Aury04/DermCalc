package com.example.dermcalc

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dermcalc.data.DermCalcDatabase
import com.example.dermcalc.data.EasiScore
import com.example.dermcalc.data.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EasiActivity : AppCompatActivity() {

    private val distretti = listOf("TESTA", "ARTI SUPERIORI", "TRONCO", "ARTI INFERIORI")
    private var indiceCorrente = 0
    private var easiTotale = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_easi)

        // Inizializziamo il DB
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
            // Validazione
            if (groups.any { it.checkedRadioButtonId == -1 }) {
                Toast.makeText(this, "Compila tutti i campi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disabilita cambio età dopo il primo inserimento per coerenza
            rgEta.getChildAt(0).isEnabled = false
            rgEta.getChildAt(1).isEnabled = false

            val isAdulto = findViewById<RadioButton>(R.id.rb_adulto).isChecked
            val pesoArea = getPesoEasi(indiceCorrente, isAdulto)

            val e = findViewById<RadioButton>(groups[0].checkedRadioButtonId).text.toString().toInt()
            val p = findViewById<RadioButton>(groups[1].checkedRadioButtonId).text.toString().toInt()
            val ex = findViewById<RadioButton>(groups[2].checkedRadioButtonId).text.toString().toInt()
            val l = findViewById<RadioButton>(groups[3].checkedRadioButtonId).text.toString().toInt()
            val areaScore = findViewById<RadioButton>(groups[4].checkedRadioButtonId).text.toString().toInt()

            // Formula EASI: (E + P + Ex + L) * AreaScore * PesoArea
            easiTotale += (e + p + ex + l) * areaScore * pesoArea

            indiceCorrente++

            if (indiceCorrente < distretti.size) {
                // Passa al prossimo distretto
                tvDistretto.text = "Distretto: ${distretti[indiceCorrente]}"
                groups.forEach { it.clearCheck() }
                if (indiceCorrente == 3) btnNext.text = "CALCOLA EASI"
            } else {
                // --- FINE DEI DISTRETTI: LOGICA DI SALVATAGGIO ---
                val cfAttivo = SessionManager.getUtenteCF(this)

                if (cfAttivo != null) {
                    val nuovoRecord = EasiScore(
                        utenteId = cfAttivo,
                        risultato = easiTotale,
                        dataCalcolo = SessionManager.getDataCorrente()
                    )

                    // Salvataggio "silenzioso" in background
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            db.dermCalcDao().insertEasi(nuovoRecord)
                        } catch (e: Exception) {
                            // Errore DB ignorato per non bloccare l'utente
                        }
                    }
                }

                // --- NAVIGAZIONE (Sempre eseguita) ---
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("EXTRA_SCORE", easiTotale)
                intent.putExtra("EXTRA_TYPE", "EASI")
                startActivity(intent)
                finish() // Chiude l'activity di calcolo
            }
        }
    }

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