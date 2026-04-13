package com.example.dermcalc

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog

class EasiActivity : AppCompatActivity() {

    private val distretti = listOf("TESTA", "ARTI SUPERIORI", "TRONCO", "ARTI INFERIORI")
    private var indiceCorrente = 0
    private var easiTotale = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_easi)

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
                tvDistretto.text = "Distretto: ${distretti[indiceCorrente]}"
                groups.forEach { it.clearCheck() }
                if (indiceCorrente == 3) btnNext.text = "CALCOLA EASI"
            } else {
                mostraRisultato()
            }
        }
    }

    private fun getPesoEasi(distrettoIdx: Int, adulto: Boolean): Double {
        return if (adulto) {
            when (distrettoIdx) {
                0 -> 0.1; 1 -> 0.2; 2 -> 0.3; else -> 0.4 // Testa, Braccia, Tronco, Gambe
            }
        } else {
            when (distrettoIdx) {
                0 -> 0.2; 1 -> 0.2; 2 -> 0.3; else -> 0.3 // Pesi diversi per bambini < 8 anni
            }
        }
    }

    private fun mostraRisultato() {
        // Logica di interpretazione basata sulla scala EASI standard
        val interpretazione = when {
            easiTotale == 0.0 -> "Pelle sana (Assente)"
            easiTotale <= 1.0 -> "Malattia quasi assente"
            easiTotale <= 7.0 -> "Dermatite Lieve"
            easiTotale <= 21.0 -> "Dermatite Moderata"
            easiTotale <= 50.0 -> "Dermatite Severa"
            else -> "Dermatite Molto Severa"
        }

        // Creazione dell'AlertDialog per mostrare il risultato finale
        AlertDialog.Builder(this)
            .setTitle("Risultato EASI")
            .setMessage("Punteggio Totale: ${String.format("%.1f", easiTotale)}\n\nInterpretazione: $interpretazione")
            .setCancelable(false) // Impedisce di chiudere cliccando fuori, obbliga a premere OK
            .setPositiveButton("TORNA ALLA HOME") { _, _ ->
                finish() // Chiude l'activity e torna alla MainActivity
            }
            .show()
    }
}