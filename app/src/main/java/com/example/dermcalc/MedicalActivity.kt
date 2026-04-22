package com.example.dermcalc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dermcalc.navBarControl.NavManager

/**
 * Activity informativa dedicata alle note metodologiche e ai disclaimer medici.
 * Fornisce all'utente il contesto scientifico dietro i calcolatori utilizzati
 * e le avvertenze legali sull'uso professionale dei risultati.
 */
class MedicalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical)
        NavManager.inizializzaNavbar(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Informazioni Mediche"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}