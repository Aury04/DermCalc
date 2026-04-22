package com.example.dermcalc.data

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Gestore della sessione utente e utility globali.
 * Utilizza un pattern Singleton per mantenere i dati dell'utente attivo in memoria RAM.
 */
object SessionManager {

    /**
     * Identificativo dell'utente loggato (Codice Fiscale).
     * Essendo una variabile privata in un 'object', i dati persistono finché
     * il processo dell'app è attivo, garantendo una sessione temporanea e sicura.
     */
    private var utenteCfAttivo: String? = null

    /**
     * Registra il Codice Fiscale dell'utente al momento del login o della registrazione.
     * @param cf Il codice fiscale dell'utente autenticato.
     */
    fun saveUtenteCF(cf: String) {
        utenteCfAttivo = cf
    }

    /**
     * Recupera il Codice Fiscale dell'utente attualmente in sessione.
     * @return Il CF dell'utente se loggato, null se l'utente sta navigando come ospite.
     */
    fun getUtenteCF(context: Context): String? {
        return utenteCfAttivo
    }

    /**
     * Termina la sessione corrente resettando il riferimento all'utente.
     * Da invocare quando l'utente sceglie di effettuare il logout.
     */
    fun logoutB(activity: AppCompatActivity) {
        utenteCfAttivo = null
    }

    /**
     * Utility per ottenere la data odierna formattata in stile italiano.
     * Utilizzata per marcare temporalmente i salvataggi dei calcoli nel database.
     * @return Stringa della data nel formato "dd/MM/yyyy".
     */
    fun getDataCorrente(): String {
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.format(date)
    }
}