package com.example.dermcalc // Assicurati che il package sia corretto

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Riferimenti ai componenti del layout
        val tvTitolo = findViewById<TextView>(R.id.tv_titolo_welcome)
        val tvSottotitolo = findViewById<TextView>(R.id.tv_sottotitolo)
        val btnOspite = findViewById<Button>(R.id.btn_ospite)
        val btnLogin = findViewById<Button>(R.id.btn_login)

        // 1. Animazione "Simpatica" (Dissolvenza in entrata)
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 1500 // 1.5 secondi
        tvTitolo.startAnimation(fadeIn)
        tvSottotitolo.startAnimation(fadeIn)

        // 2. Azione Bottone "Continua come ospite"
        btnOspite.setOnClickListener {
            // Sostituisci 'MainActivity' con il nome della tua Activity Home
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 3. Azione Bottone "Login o Registrati"
        btnLogin.setOnClickListener {
            // Qui potrai collegare la pagina di login quando la creeremo
            // Per ora mostriamo un messaggio o lasciamo pronto il codice
        }
    }
}