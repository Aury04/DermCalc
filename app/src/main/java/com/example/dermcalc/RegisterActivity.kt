package com.example.dermcalc

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dermcalc.databinding.ActivityRegisterBinding
import com.example.dermcalc.data.DermCalcDatabase
import com.example.dermcalc.data.Utente
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = DermCalcDatabase.getDatabase(this)

        binding.btnSalvaRegistrazione.setOnClickListener {
            // Recupero dati dai campi
            val nome = binding.regNome.text.toString().trim()
            val cognome = binding.regCognome.text.toString().trim()
            val cf = binding.regCF.text.toString().trim().uppercase()
            val email = binding.regEmail.text.toString().trim()
            val pass = binding.regPass.text.toString().trim()

            // Validazione semplice
            if (nome.isEmpty() || cognome.isEmpty() || cf.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Tutti i campi sono obbligatori", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Creazione oggetto Utente
            val nuovoUtente = Utente(
                codFiscale = cf,
                nome = nome,
                cognome = cognome,
                email = email,
                password = pass
            )

            // Salvataggio nel DB
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    db.dermCalcDao().insertUtente(nuovoUtente)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegisterActivity, "Registrazione avvenuta con successo!", Toast.LENGTH_SHORT).show()
                        // Dopo la registrazione torniamo al Login
                        finish()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegisterActivity, "Errore: Forse il Codice Fiscale è già registrato?", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        binding.tvTornaAlLogin.setOnClickListener {
            finish() // Chiude e torna alla schermata di Login
        }
    }
}