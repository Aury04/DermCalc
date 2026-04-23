package com.example.dermcalc.navBarControl

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dermcalc.*
import com.example.dermcalc.data.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Gestore centralizzato della barra di navigazione (BottomNavigationView).
 * Permette di mantenere una logica di navigazione coerente tra tutte le schermate
 * dell'applicazione senza duplicare il codice.
 */
object NavManager {

    /**
     * Inizializza la Navbar, configura i listener per i click e corregge il focus visivo.
     * Deve essere chiamata nel metodo onCreate di ogni Activity che implementa la barra.
     * @param activity L'activity di riferimento da cui viene invocata la navbar.
     */
    fun inizializzaNavbar(activity: AppCompatActivity) {
        val bottomNav = activity.findViewById<BottomNavigationView>(R.id.bottom_navigation)

        /**
         * --- RESET SELEZIONE INIZIALE ---
         * Disattiva l'evidenziazione automatica del primo elemento per mantenere
         * la barra in uno stato neutro all'apertura delle calcolatrici.
         */
        bottomNav.menu.setGroupCheckable(0, true, false)
        for (i in 0 until bottomNav.menu.size()) {
            bottomNav.menu.getItem(i).isChecked = false
        }
        bottomNav.menu.setGroupCheckable(0, true, true)

        // --- GESTIONE CLICK SUI MENU---
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_info -> {
                    val intent = Intent(activity, MedicalActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    activity.startActivity(intent)
                    false
                }
                R.id.nav_home -> {
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    activity.startActivity(intent)
                    true
                }
                R.id.nav_user -> {
                    gestisciProfilo(activity)
                    false
                }
                else -> false
            }
        }
    }

    /**
     * Gestisce la logica di accesso all'area utente.
     * Se l'utente è loggato, mostra opzioni di visualizzazione dati o logout.
     * Se l'utente è un ospite, fornisce un invito rapido alla registrazione.
     */
    private fun gestisciProfilo(activity: AppCompatActivity) {
        val cfLoggato = SessionManager.getUtenteCF(activity)

        if (cfLoggato != null) {
            // --- SCENARIO: UTENTE AUTENTICATO ---
            AlertDialog.Builder(activity)
                .setTitle("Gestione Account")
                .setMessage("Sei connesso come: $cfLoggato")
                .setPositiveButton("Visualizza Dati") { _, _ ->
                    val intent = Intent(activity, ProfileActivity::class.java)
                    activity.startActivity(intent)
                }
                .setNeutralButton("Logout") { _, _ ->
                    SessionManager.logoutB(activity)
                    val intent = Intent(activity, WelcomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    activity.startActivity(intent)
                }
                .setNegativeButton("Annulla", null)
                .show()
        } else {
            // --- SCENARIO: NAVIGAZIONE OSPITE ---
            AlertDialog.Builder(activity)
                .setTitle("Accesso Ospite")
                .setMessage("Registrati o accedi")
                .setPositiveButton("Vai alla Registrazione") { _, _ ->
                    activity.startActivity(Intent(activity, RegisterActivity::class.java))
                }
                .setNegativeButton("Continua come ospite", null)
                .show()
        }
    }
}