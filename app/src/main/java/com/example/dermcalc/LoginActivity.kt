package com.example.dermcalc

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dermcalc.databinding.ActivityLoginBinding
import com.example.dermcalc.data.DermCalcDatabase
import com.example.dermcalc.data.SessionManager
import com.example.dermcalc.controlli.ControlliRL // Importiamo la tua classe dei controlli
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

            // --- NUOVA LOGICA: CRIPTAZIONE PER IL CONFRONTO ---
            // Trasformiamo la password inserita nello stesso Hash usato in registrazione
            val passwordCriptata = ControlliRL.hashPassword(passwordInserita)

            lifecycleScope.launch(Dispatchers.IO) {
                // Cerchiamo l'utente usando CF e la password CRIPTATA
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
                        // Se l'utente non viene trovato, potrebbe essere il CF sbagliato
                        // o la password che, una volta criptata, non corrisponde a quella nel DB
                        Toast.makeText(this@LoginActivity, "Codice Fiscale o Password errati", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.tvVaiARegistrati.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.tvTornaIndietro.setOnClickListener {
            finish()
        }
    }
}