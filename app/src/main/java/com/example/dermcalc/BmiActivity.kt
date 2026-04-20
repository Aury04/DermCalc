package com.example.dermcalc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope // NUOVO: serve per le coroutine
import com.example.dermcalc.NavBarControl.NavManager
import com.example.dermcalc.data.DermCalcDatabase
import com.example.dermcalc.data.BmiScore
import com.example.dermcalc.data.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BmiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi)

        NavManager.inizializzaNavbar(this)

        // Inizializziamo il DB
        val db = DermCalcDatabase.getDatabase(this) // NUOVO

        val etPeso = findViewById<EditText>(R.id.et_peso)
        val etAltezza = findViewById<EditText>(R.id.et_altezza)
        val btnCalcola = findViewById<Button>(R.id.btn_calcola_bmi)

        btnCalcola.setOnClickListener {
            val pesoStr = etPeso.text.toString()
            val altezzaStr = etAltezza.text.toString()

            if (pesoStr.isEmpty() || altezzaStr.isEmpty()) {
                Toast.makeText(this, "Inserisci tutti i dati!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val peso = pesoStr.toDouble()
            val altezzaCm = altezzaStr.toDouble()

            if (altezzaCm <= 0 || peso <= 0) {
                Toast.makeText(this, "Dati non validi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val altezzaM = altezzaCm / 100
            val bmi = peso / (altezzaM * altezzaM)

            // 1. RECUPERIAMO IL CF (se esiste)
            val cfAttivo = SessionManager.getUtenteCF(this)

            // 2. LOGICA DI SALVATAGGIO (Solo se loggato)
            if (cfAttivo != null) {
                val nuovoRecord = BmiScore(
                    utenteId = cfAttivo,
                    risultato = bmi,
                    dataCalcolo = SessionManager.getDataCorrente()
                )

                // Salvataggio "silenzioso" in background (non blocca l'utente)
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        db.dermCalcDao().insertBmi(nuovoRecord)
                    } catch (e: Exception) {
                        // Errore silenzioso o log
                    }
                }
            }
            // 3. NAVIGAZIONE AI RISULTATI (Sempre, anche se non loggato)
            val intent = Intent(this@BmiActivity, ResultActivity::class.java)
            intent.putExtra("EXTRA_SCORE", bmi)
            intent.putExtra("EXTRA_TYPE", "BMI")
            startActivity(intent)
        }


    }
}