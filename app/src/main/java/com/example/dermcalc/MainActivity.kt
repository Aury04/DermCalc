package com.example.dermcalc

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity // Cambiato da ComponentActivity
import android.widget.Button
import com.example.dermcalc.NavBarControl.NavManager

// Usiamo AppCompatActivity per supportare i layout XML classici
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Questo comando collega il file XML che abbiamo creato
        setContentView(R.layout.activity_main)
        NavManager.inizializzaNavbar(this)

        // Esempio di come collegare i bottoni e aggiungere un'azione
        val btnPasi = findViewById<Button>(R.id.btn_pasi)
        btnPasi.setOnClickListener {
            val intent = Intent(this, PasiActivity::class.java)
            startActivity(intent)
        }

        val btnEasi = findViewById<Button>(R.id.btn_easi)
        btnEasi.setOnClickListener {
            val intent = Intent(this, EasiActivity::class.java)
            startActivity(intent)
        }

        val btnBmi = findViewById<Button>(R.id.btn_bmi)
        btnBmi.setOnClickListener {
            val intent = Intent(this, BmiActivity::class.java)
            startActivity(intent)
        }

        val btnBsa = findViewById<Button>(R.id.btn_bsa)
        btnBsa.setOnClickListener {
            val intent = Intent(this, BsaActivity::class.java)
            startActivity(intent)
        }

    }
}