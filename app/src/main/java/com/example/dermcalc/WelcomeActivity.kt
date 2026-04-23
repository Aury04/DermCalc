package com.example.dermcalc

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Schermata di Benvenuto (Landing Page).
 * È la prima Activity visualizzata all'apertura dell'app. Permette di scegliere
 * tra una navigazione libera (Ospite) o l'accesso/registrazione all'area riservata.
 */
class WelcomeActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val tvTitolo = findViewById<TextView>(R.id.tv_titolo_welcome)
        val tvSottotitolo = findViewById<TextView>(R.id.tv_sottotitolo)
        val btnOspite = findViewById<Button>(R.id.btn_ospite)
        val btnLogin = findViewById<Button>(R.id.btn_login)

        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 1500
        tvTitolo.startAnimation(fadeIn)
        tvSottotitolo.startAnimation(fadeIn)

        /**
         * --- MODALITÀ OSPITE ---
         * Permette l'accesso immediato alle calcolatrici.
         * In questa modalità, i risultati non verranno salvati nel database
         * poiché il SessionManager non avrà un Codice Fiscale di riferimento.
         */
        btnOspite.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        /**
         * --- 3. ACCESSO AREA RISERVATA ---
         * Reindirizza alla LoginActivity per l'autenticazione.
         * Indispensabile per gli utenti che desiderano mantenere lo storico dei calcoli.
         */
        btnLogin.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}