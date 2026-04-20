package com.example.dermcalc.data

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object SessionManager {
    private const val PREFS_NAME = "DermCalcPrefs"
    private const val KEY_CF = "utente_cf"

    // Recupera il CF dell'utente loggato
    fun getUtenteCF(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_CF, null)
    }

    // Cancella la sessione (Logout)
    fun logout(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    fun getDataCorrente(): String {
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.format(date)
    }

}