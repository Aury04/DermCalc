package com.example.dermcalc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dermcalc.NavBarControl.NavManager
import com.example.dermcalc.data.DermCalcDatabase
import com.example.dermcalc.data.SessionManager
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    // Definiamo una classe semplice per unificare i calcoli diversi nella lista
    data class CalcoloUnificato(
        val tipo: String,
        val valore: String,
        val data: String // Usiamo il timestamp per l'ordinamento
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        NavManager.inizializzaNavbar(this)

        val cfLoggato = SessionManager.getUtenteCF(this)

        if (cfLoggato != null) {
            val db = DermCalcDatabase.getDatabase(this).dermCalcDao()

            lifecycleScope.launch {
                // 1. Carica dati utente
                val utente = db.getUtente(cfLoggato)
                utente?.let {
                    findViewById<TextView>(R.id.tv_full_name).text = "${it.nome} ${it.cognome}"
                    findViewById<TextView>(R.id.tv_cf).text = "Codice Fiscale: ${it.codFiscale}"
                    findViewById<TextView>(R.id.tv_email).text = "Email: ${it.email}"
                }

                // 2. Recupera tutti i calcoli dai vari metodi del DAO
                val listaPasi = db.getPasiByUser(cfLoggato).map { CalcoloUnificato("PASI", it.risultato.toString(), it.dataCalcolo) }
                val listaEasi = db.getEasiByUser(cfLoggato).map { CalcoloUnificato("EASI", it.risultato.toString(), it.dataCalcolo) }
                val listaBmi = db.getBmiByUser(cfLoggato).map { CalcoloUnificato("BMI", it.risultato.toString(), it.dataCalcolo) }
                val listaBsa = db.getBsaByUser(cfLoggato).map { CalcoloUnificato("BSA", "${it.risultato} m²", it.dataCalcolo) }

                // 3. Unisci e ordina per data (decrescente)
                val storicoCompleto = (listaPasi + listaEasi + listaBmi + listaBsa)
                    .sortedByDescending { it.data }

                // 4. Mostra i dati nell'interfaccia
                popolaInterfaccia(storicoCompleto)
            }
        }
    }

    private fun popolaInterfaccia(lista: List<CalcoloUnificato>) {
        val container = findViewById<LinearLayout>(R.id.container_history)
        container.removeAllViews()

        if (lista.isEmpty()) {
            val emptyView = TextView(this)
            emptyView.text = "Nessun calcolo effettuato finora."
            container.addView(emptyView)
            return
        }

        val inflater = LayoutInflater.from(this)
        for (item in lista) {
            val view = inflater.inflate(R.layout.item_storico_calcolo, container, false)

            view.findViewById<TextView>(R.id.tv_tipo_calcolo).text = item.tipo
            view.findViewById<TextView>(R.id.tv_risultato).text = "Valore: ${item.valore}"
            view.findViewById<TextView>(R.id.tv_data_calcolo).text = item.data

            container.addView(view)
        }
    }
}