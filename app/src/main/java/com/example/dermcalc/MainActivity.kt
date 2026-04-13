package com.example.dermcalc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity // Cambiato da ComponentActivity
import android.widget.Button
import android.widget.Toast

// Usiamo AppCompatActivity per supportare i layout XML classici
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Questo comando collega il file XML che abbiamo creato
        setContentView(R.layout.activity_main)

        // Esempio di come collegare i bottoni e aggiungere un'azione
        val btnPasi = findViewById<Button>(R.id.btn_pasi)
        btnPasi.setOnClickListener {
            // Per ora mostriamo solo un messaggio, poi qui caricherai la nuova schermata
            Toast.makeText(this, "Apertura Calcolatore PASI", Toast.LENGTH_SHORT).show()
        }
    }
}