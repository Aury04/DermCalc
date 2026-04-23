package com.example.dermcalc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dermcalc.navBarControl.NavManager
import com.example.dermcalc.data.DermCalcDatabase
import com.example.dermcalc.data.SessionManager
import kotlinx.coroutines.launch

/**
 * Activity dedicata al profilo utente e alla visualizzazione dello storico.
 * Estrae dati da tabelle multiple del database e li unifica in un'unica
 * cronologia ordinata per data.
 */
class ProfileActivity : AppCompatActivity() {

    /**
     * Rappresentazione generica di un risultato clinico.
     * Utilizzata per normalizzare i dati di PASI, EASI, BMI e BSA in un unico formato
     * adatto alla visualizzazione nella lista dello storico.
     */
    data class CalcoloUnificato(
        val tipo: String,
        val valore: String,
        val data: String,
        val peso: String? = null,
        val altezza: String? = null
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        NavManager.inizializzaNavbar(this)

        val cfLoggato = SessionManager.getUtenteCF(this)

        if (cfLoggato != null) {
            val db = DermCalcDatabase.getDatabase(this).dermCalcDao()

            /**
             * Utilizzo di lifecycleScope: permette di eseguire query pesanti (multiple)
             * in modo asincrono, evitando di bloccare l'interfaccia durante il caricamento
             * dello storico completo.
             */
            lifecycleScope.launch {
                val utente = db.getUtente(cfLoggato)
                utente?.let {
                    findViewById<TextView>(R.id.tv_full_name).text = "${it.nome} ${it.cognome}"
                    findViewById<TextView>(R.id.tv_cf).text = "Codice Fiscale: ${it.codFiscale}"
                    findViewById<TextView>(R.id.tv_email).text = "Email: ${it.email}"
                }

                val listaPasi = db.getPasiByUser(cfLoggato).map {
                    CalcoloUnificato("PASI", String.format("%.2f", it.risultato), it.dataCalcolo)
                }
                val listaEasi = db.getEasiByUser(cfLoggato).map {
                    CalcoloUnificato("EASI", String.format("%.2f", it.risultato), it.dataCalcolo)
                }
                val listaBmi = db.getBmiByUser(cfLoggato).map {
                    CalcoloUnificato(
                        "BMI",
                        String.format("%.2f", it.risultato),
                        it.dataCalcolo,
                        it.peso.toString(),
                        it.altezza.toString()
                    )
                }
                val listaBsa = db.getBsaByUser(cfLoggato).map {
                    CalcoloUnificato(
                        "BSA",
                        String.format("%.2f m²", it.risultato),
                        it.dataCalcolo,
                        it.peso.toString(),
                        it.altezza.toString()
                    )
                }
                val storicoCompleto = (listaPasi + listaEasi + listaBmi + listaBsa)
                    .sortedByDescending { it.data }

                popolaInterfaccia(storicoCompleto)
            }
        }
    }

    /**
     * Genera dinamicamente le righe dello storico all'interno del container.
     * Utilizza un LayoutInflater per inserire un file XML (item_storico_calcolo)
     * per ogni risultato trovato.
     */
    private fun popolaInterfaccia(lista: List<CalcoloUnificato>) {
        val container = findViewById<LinearLayout>(R.id.container_history)
        container.removeAllViews()

        if (lista.isEmpty()) {
            val emptyView = TextView(this)
            emptyView.text = "Nessun calcolo effettuato finora."
            emptyView.setPadding(0, 20, 0, 0)
            container.addView(emptyView)
            return
        }

        val inflater = LayoutInflater.from(this)
        for (item in lista) {
            val view = inflater.inflate(R.layout.item_storico_calcolo, container, false)

            val tvTipo = view.findViewById<TextView>(R.id.tv_tipo_calcolo)
            val tvRisultato = view.findViewById<TextView>(R.id.tv_risultato)
            val tvData = view.findViewById<TextView>(R.id.tv_data_calcolo)
            val indicator = view.findViewById<android.view.View>(R.id.indicator_type)

            val layoutFisico = view.findViewById<LinearLayout>(R.id.layout_dettagli_fisici)
            val tvPeso = view.findViewById<TextView>(R.id.tv_peso)
            val tvAltezza = view.findViewById<TextView>(R.id.tv_altezza)

            val coloreSettato = when (item.tipo.uppercase()) {
                "PASI" -> android.graphics.Color.parseColor("#0D47A1")
                "EASI" -> android.graphics.Color.parseColor("#1565C0")
                "BMI"  -> android.graphics.Color.parseColor("#1976D2")
                "BSA"  -> android.graphics.Color.parseColor("#1E88E5")
                else   -> android.graphics.Color.GRAY
            }

            tvTipo.text = item.tipo
            tvTipo.setTextColor(coloreSettato)
            tvRisultato.text = "Valore: ${item.valore}"
            tvData.text = item.data
            indicator.setBackgroundColor(coloreSettato)

            if (item.peso != null && item.altezza != null) {
                layoutFisico.visibility = View.VISIBLE
                tvPeso.text = "Peso: ${item.peso} kg"
                tvAltezza.text = "Altezza: ${item.altezza} cm"
            } else {
                layoutFisico.visibility = View.GONE
            }

            container.addView(view)
        }
    }
}