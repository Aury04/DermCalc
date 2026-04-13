package com.example.dermcalc

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class BmiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi)

        val etPeso = findViewById<EditText>(R.id.et_peso)
        val etAltezza = findViewById<EditText>(R.id.et_altezza)
        val btnCalcola = findViewById<Button>(R.id.btn_calcola_bmi)

        btnCalcola.setOnClickListener {
            val pesoStr = etPeso.text.toString()
            val altezzaStr = etAltezza.text.toString()

            // Gestione degli errori: verifica campi vuoti
            if (pesoStr.isEmpty() || altezzaStr.isEmpty()) {
                Toast.makeText(this, "Inserisci tutti i dati!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val peso = pesoStr.toDouble()
            val altezzaCm = altezzaStr.toDouble()

            if (altezzaCm <= 0) {
                Toast.makeText(this, "Altezza non valida!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (peso <= 0) {
                Toast.makeText(this, "Peso non valido!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Conversione altezza in metri per la formula
            val altezzaM = altezzaCm / 100
            val bmi = peso / (altezzaM * altezzaM)

            mostraRisultato(bmi)
        }
    }

    private fun mostraRisultato(bmi: Double) {
        val categoria = when {
            bmi < 18.5 -> "Sottopeso"
            bmi < 25.0 -> "Normopeso"
            bmi < 30.0 -> "Sovrappeso"
            else -> "Obesità"
        }

        AlertDialog.Builder(this)
            .setTitle("Risultato BMI")
            .setMessage("Il tuo BMI è: ${String.format("%.2f", bmi)}\nCategoria: $categoria")
            .setPositiveButton("OK") { _, _ -> finish() }
            .show()
    }
}