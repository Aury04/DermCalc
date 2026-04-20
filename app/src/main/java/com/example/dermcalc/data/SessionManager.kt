package com.example.dermcalc.data

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object SessionManager {

    // --- QUESTA È LA CHIAVE ---
    // Usiamo una variabile semplice invece delle SharedPreferences.
    // Essendo un 'object', questa variabile è globale per l'app,
    // ma scompare non appena l'app viene chiusa del tutto.
    private var utenteCfAttivo: String? = null

    // 1. Funzione per salvare (da chiamare nel Login)
    fun saveUtenteCF(cf: String) {
        utenteCfAttivo = cf
    }

    // 2. Funzione per recuperare (da chiamare nei calcoli BMI, BSA, ecc.)
    fun getUtenteCF(context: Context): String? {
        return utenteCfAttivo
    }

    // 3. Funzione Logout (per il tasto esci o per resettare)
    fun logout() {
        utenteCfAttivo = null
    }

    // Funzione per la data (rimane uguale)
    fun getDataCorrente(): String {
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.format(date)
    }
}