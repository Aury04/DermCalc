package com.example.dermcalc

import android.content.Intent
import android.os.Bundle
import android.graphics.Color
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dermcalc.navBarControl.NavManager

/**
 * Activity di visualizzazione dei risultati.
 * Riceve il punteggio calcolato e il tipo di test effettuato,
 * applica le scale di gravità mediche internazionali e fornisce
 * un feedback visivo immediato tramite colori e descrizioni.
 */
class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        NavManager.inizializzaNavbar(this)

        val tvTitle = findViewById<TextView>(R.id.tv_result_title)
        val tvScore = findViewById<TextView>(R.id.tv_score_value)
        val tvDesc = findViewById<TextView>(R.id.tv_score_description)
        val btnHome = findViewById<Button>(R.id.btn_back_home)

        /**
         * --- RECUPERO DATI DALL'INTENT ---
         * EXTRA_SCORE: il valore numerico calcolato
         * EXTRA_TYPE: la stringa identificativa (BMI, BSA, PASI, EASI)
         */
        val score = intent.getDoubleExtra("EXTRA_SCORE", 0.0)
        val type = intent.getStringExtra("EXTRA_TYPE") ?: "Calcolo"

        tvTitle.text = "Risultato $type"
        tvScore.text = String.format("%.2f", score)

        /**
         * --- LOGICA DI CLASSIFICAZIONE CLINICA ---
         * Per ogni tipologia di test, vengono definiti i "cutoff" basati
         * sulla letteratura scientifica e associati a un codice colore semaforico.
         */
        when (type) {
            "BMI" -> {
                when {
                    score < 18.5 -> { tvDesc.text = "Sottopeso"; tvDesc.setTextColor(Color.BLUE) }
                    score < 25.0 -> { tvDesc.text = "Normopeso"; tvDesc.setTextColor(Color.GREEN) }
                    score < 30.0 -> { tvDesc.text = "Sovrappeso"; tvDesc.setTextColor(Color.MAGENTA) }
                    else -> { tvDesc.text = "Obesità"; tvDesc.setTextColor(Color.RED) }
                }
            }
            "BSA" -> {
                tvDesc.text = "Superficie Corporea Totale in m²"
                tvDesc.setTextColor(Color.BLACK)
            }
            "PASI" -> {
                when {
                    score < 10 -> { tvDesc.text = "Psoriasi Lieve"; tvDesc.setTextColor(Color.GREEN) }
                    score <= 20 -> { tvDesc.text = "Psoriasi Moderata"; tvDesc.setTextColor(Color.MAGENTA) }
                    else -> { tvDesc.text = "Psoriasi Grave"; tvDesc.setTextColor(Color.RED) }
                }
            }
            "EASI" -> {
                when {
                    score <= 1.0 -> { tvDesc.text = "Dermatite Assente"; tvDesc.setTextColor(Color.GRAY) }
                    score <= 7.0 -> { tvDesc.text = "Dermatite Lieve"; tvDesc.setTextColor(Color.GREEN) }
                    score <= 21.0 -> { tvDesc.text = "Dermatite Moderata"; tvDesc.setTextColor(Color.MAGENTA) }
                    else -> { tvDesc.text = "Dermatite Grave"; tvDesc.setTextColor(Color.RED) }
                }
            }
        }

        btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}