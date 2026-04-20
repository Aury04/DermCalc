package com.example.dermcalc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dermcalc.NavBarControl.NavManager


class MedicalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical)
        NavManager.inizializzaNavbar(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Informazioni Mediche"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // Chiude questa pagina e torna alla precedente
        return true
    }

}