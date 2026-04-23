package com.example.dermcalc

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.example.dermcalc.navBarControl.NavManager

/**
 * Main Entry Point dell'applicazione dopo il Login/Welcome.
 * Questa Activity funge da Dashboard principale, esponendo i quattro calcolatori
 * clinici core del progetto: PASI, EASI, BMI e BSA.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        NavManager.inizializzaNavbar(this)

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