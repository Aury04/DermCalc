package com.example.dermcalc.NavBarControl

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dermcalc.*
import com.example.dermcalc.data.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView

object NavManager {

    /**
     * Inizializza la Navbar inferiore, gestisce la navigazione e
     * corregge il problema dell'icona inizialmente selezionata.
     */
    fun inizializzaNavbar(activity: AppCompatActivity) {
        val bottomNav = activity.findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // --- RESET SELEZIONE INIZIALE ---
        // Di default la Navbar seleziona il primo elemento.
        // Questo blocco deseleziona tutto all'avvio dell'Activity.
        bottomNav.menu.setGroupCheckable(0, true, false)
        for (i in 0 until bottomNav.menu.size()) {
            bottomNav.menu.getItem(i).isChecked = false
        }
        bottomNav.menu.setGroupCheckable(0, true, true)

        // --- GESTIONE CLICK ---
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_info -> {
                    val intent = Intent(activity, MedicalActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    activity.startActivity(intent)
                    false
                }
                R.id.nav_home -> {
                    // Torna alla MainActivity e pulisce lo stack delle Activity
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    activity.startActivity(intent)
                    true
                }
                R.id.nav_user -> {
                    // Gestisce il profilo (Logout o invito a Registrazione)
                    gestisciProfilo(activity)
                    // Ritorna false per non lasciare l'icona "accesa" dopo il click
                    false
                }
                else -> false
            }
        }
    }

    /**
     * Mostra un popup differente in base allo stato di login dell'utente.
     */
    private fun gestisciProfilo(activity: AppCompatActivity) {
        val cfLoggato = SessionManager.getUtenteCF(activity)

        if (cfLoggato != null) {
            // Caso 1: UTENTE LOGGATO
            AlertDialog.Builder(activity)
                .setTitle("Gestione Account")
                .setMessage("Sei connesso come: $cfLoggato")
                .setPositiveButton("Visualizza Dati") { _, _ ->
                    // Qui puoi aggiungere l'intent per la pagina dei risultati salvati
                    // activity.startActivity(Intent(activity, StoricoActivity::class.java))
                }
                .setNeutralButton("Logout") { _, _ ->
                    // Esegue il logout e torna alla pagina di Welcome
                    SessionManager.logoutB(activity)
                    val intent = Intent(activity, WelcomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    activity.startActivity(intent)
                }
                .setNegativeButton("Annulla", null)
                .show()
        } else {
            // Caso 2: UTENTE NON LOGGATO (Ospite)
            AlertDialog.Builder(activity)
                .setTitle("Accesso Ospite")
                .setMessage("Registrati o accedi per salvare i tuoi calcoli nel database!")
                .setPositiveButton("Vai alla Registrazione") { _, _ ->
                    activity.startActivity(Intent(activity, RegisterActivity::class.java))
                }
                .setNegativeButton("Continua come ospite", null)
                .show()
        }
    }
}