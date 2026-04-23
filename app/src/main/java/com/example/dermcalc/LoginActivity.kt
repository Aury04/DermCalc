package com.example.dermcalc

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dermcalc.databinding.ActivityLoginBinding
import com.example.dermcalc.data.DermCalcDatabase
import com.example.dermcalc.data.SessionManager
import com.example.dermcalc.controls.RLControls // Importiamo la tua classe dei controlli
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Activity che gestisce l'autenticazione dell'utente.
 * Implementa il controllo delle credenziali tramite confronto di hash crittografici
 * e inizializza la sessione globale in caso di successo.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = DermCalcDatabase.getDatabase(this)

        binding.btnLogin.setOnClickListener {
            val cf = binding.etCF.text.toString().trim().uppercase()
            val passwordInserita = binding.etPassword.text.toString().trim()

            if (cf.isEmpty() || passwordInserita.isEmpty()) {
                Toast.makeText(this, "Per favore, compila tutti i campi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            /**
             * --- SICUREZZA: HASHING ---
             * Non inviamo la password reale al database. Trasformiamo l'input in un hash SHA-256.
             * Se l'hash generato corrisponde a quello salvato nel DB, l'identità è confermata.
             */
            val passwordCriptata = RLControls.hashPassword(passwordInserita)

            lifecycleScope.launch(Dispatchers.IO) {
                val utente = db.dermCalcDao().login(cf, passwordCriptata)

                withContext(Dispatchers.Main) {
                    if (utente != null) {
                        SessionManager.saveUtenteCF(cf)

                        Toast.makeText(this@LoginActivity, "Bentornato, ${utente.nome}!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Codice Fiscale o Password errati", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Navigazione verso la schermata di registrazione
        binding.tvVaiARegistrati.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Ritorno alla schermata di benvenuto/scelta iniziale
        binding.tvTornaWelcome.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }
    }
}