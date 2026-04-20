package com.example.dermcalc

import android.content.Intent
import android.os.Bundle
import android.graphics.Color
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dermcalc.controlloNavBar.NavManager

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        NavManager.inizializzaNavbar(this)

        val tvTitle = findViewById<TextView>(R.id.tv_result_title)
        val tvScore = findViewById<TextView>(R.id.tv_score_value)
        val tvDesc = findViewById<TextView>(R.id.tv_score_description)
        val btnHome = findViewById<Button>(R.id.btn_back_home)

        // Recuperiamo i dati passati dalle altre Activity
        val score = intent.getDoubleExtra("EXTRA_SCORE", 0.0)
        val type = intent.getStringExtra("EXTRA_TYPE") ?: "Calcolo"

        tvTitle.text = "Risultato $type"
        tvScore.text = String.format("%.2f", score)

        // Logica di classificazione per ogni funzionalità
        when (type) {
            "BMI" -> {
                when {
                    score < 18.5 -> { tvDesc.text = "Sottopeso"; tvDesc.setTextColor(Color.BLUE) }
                    score < 25.0 -> { tvDesc.text = "Normopeso"; tvDesc.setTextColor(Color.GREEN) }
                    score < 30.0 -> { tvDesc.text = "Sovrappeso"; tvDesc.setTextColor(Color.YELLOW) }
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
                    score <= 20 -> { tvDesc.text = "Psoriasi Moderata"; tvDesc.setTextColor(Color.YELLOW) }
                    else -> { tvDesc.text = "Psoriasi Grave"; tvDesc.setTextColor(Color.RED) }
                }
            }
            "EASI" -> {
                when {
                    score <= 1.0 -> { tvDesc.text = "Dermatite Assente"; tvDesc.setTextColor(Color.GRAY) }
                    score <= 7.0 -> { tvDesc.text = "Dermatite Lieve"; tvDesc.setTextColor(Color.GREEN) }
                    score <= 21.0 -> { tvDesc.text = "Dermatite Moderata"; tvDesc.setTextColor(Color.YELLOW) }
                    else -> { tvDesc.text = "Dermatite Grave"; tvDesc.setTextColor(Color.RED) }
                }
            }
        }

        btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // Queste "Flags" dicono ad Android di chiudere tutte le altre attività aperte
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish() // Chiude definitivamente la schermata dei risultati
        }
    }
}