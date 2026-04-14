package com.example.dermcalc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt

class BsaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bsa)

        val etAltezza = findViewById<EditText>(R.id.et_altezza_bsa)
        val etPeso = findViewById<EditText>(R.id.et_peso_bsa)
        val btnCalcola = findViewById<Button>(R.id.btn_calcola_bsa)

        btnCalcola.setOnClickListener {
            val altezzaStr = etAltezza.text.toString()
            val pesoStr = etPeso.text.toString()

            if (altezzaStr.isEmpty() || pesoStr.isEmpty()) {
                Toast.makeText(this, "Per favore, inserisci tutti i dati", Toast.LENGTH_SHORT).show()
            }else if(altezzaStr.toDouble()<=0){
                Toast.makeText(this, "Altezza non valida!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if(pesoStr.toDouble()<=0){
                Toast.makeText(this, "Peso non valido!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else {
                val altezza = altezzaStr.toDouble()
                val peso = pesoStr.toDouble()

                // Calcolo BSA con formula di Mosteller
                val bsa = sqrt((altezza * peso) / 3600)

                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("EXTRA_SCORE", bsa)
                intent.putExtra("EXTRA_TYPE", "BSA")
                startActivity(intent)
            }
        }
    }

    private fun mostraRisultato(valoreBsa: Double) {
        val bsaFormattato = String.format("%.2f", valoreBsa)

        AlertDialog.Builder(this)
            .setTitle("Risultato BSA")
            .setMessage("La superficie corporea stimata è di:\n\n$bsaFormattato m²")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}