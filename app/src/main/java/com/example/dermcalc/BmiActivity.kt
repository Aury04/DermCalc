package com.example.dermcalc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dermcalc.navBarControl.NavManager
import com.example.dermcalc.data.DermCalcDatabase
import com.example.dermcalc.data.BmiScore
import com.example.dermcalc.data.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Activity dedicata al calcolo dell'Indice di Massa Corporea (BMI).
 * Gestisce l'acquisizione dei dati antropometrici, il calcolo matematico
 * e il salvataggio asincrono dei risultati per gli utenti loggati.
 */
class BmiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi)

        NavManager.inizializzaNavbar(this)

        val db = DermCalcDatabase.getDatabase(this)

        val etPeso = findViewById<EditText>(R.id.et_peso)
        val etAltezza = findViewById<EditText>(R.id.et_altezza)
        val btnCalcola = findViewById<Button>(R.id.btn_calcola_bmi)

        btnCalcola.setOnClickListener {
            val pesoStr = etPeso.text.toString()
            val altezzaStr = etAltezza.text.toString()

            // --- VALIDAZIONE INPUT ---
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

            /**
             * --- LOGICA DI CALCOLO ---
             * Formula BMI: Peso (kg) / Altezza^2 (m)
            */
            val altezzaM = altezzaCm / 100
            val bmi = peso / (altezzaM * altezzaM)

            /**
             * --- GESTIONE SESSIONE E SALVATAGGIO ---
             * Recuperiamo il Codice Fiscale dalla sessione RAM (null se ospite)
             */
            val cfAttivo = SessionManager.getUtenteCF(this)

            if (cfAttivo != null) {
                val nuovoRecord = BmiScore(
                    utenteId = cfAttivo,
                    risultato = bmi,
                    dataCalcolo = SessionManager.getDataCorrente(),
                    peso = peso,
                    altezza = altezzaM
                )

                /**
                 * Salvataggio in Background:
                 * lifecycleScope.launch(Dispatchers.IO) per eseguire la query
                 * su un thread dedicato all'I/O. Questo evita il blocco dell'interfaccia
                 * utente (ANR - App Not Responding) durante la scrittura su disco.
                 */
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        db.dermCalcDao().insertBmi(nuovoRecord)
                    } catch (e: Exception) {

                    }
                }
            }

            /**
             *  --- NAVIGAZIONE AI RISULTATI ---
             * Passiamo il valore calcolato e il tipo di calcolo alla ResultActivity
             */
            val intent = Intent(this@BmiActivity, ResultActivity::class.java)
            intent.putExtra("EXTRA_SCORE", bmi)
            intent.putExtra("EXTRA_TYPE", "BMI")
            startActivity(intent)
        }


    }
}