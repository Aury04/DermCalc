package com.example.dermcalc // Assicurati che questo corrisponda al tuo progetto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dermcalc.databinding.ActivityLoginBinding
import com.example.dermcalc.data.DermCalcDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    // Utilizziamo ViewBinding per accedere agli elementi dell'XML
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inizializziamo l'istanza del Database
        val db = DermCalcDatabase.getDatabase(this)

        // 1. GESTIONE DEL TASTO LOGIN
        binding.btnLogin.setOnClickListener {
            val cf = binding.etCF.text.toString().trim().uppercase()
            val password = binding.etPassword.text.toString().trim()

            // Controllo rapido che i campi non siano vuoti
            if (cf.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Per favore, compila tutti i campi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Operazione su Database (Thread IO) tramite Coroutine
            lifecycleScope.launch(Dispatchers.IO) {
                val utente = db.dermCalcDao().login(cf, password)

                // Torniamo sul thread principale (Main) per aggiornare l'interfaccia
                withContext(Dispatchers.Main) {
                    if (utente != null) {
                        // --- SALVATAGGIO SESSIONE (Il nostro "Cookie") ---
                        val sharedPref = getSharedPreferences("DermCalcPrefs", MODE_PRIVATE)
                        sharedPref.edit().putString("utente_cf", cf).apply()

                        Toast.makeText(this@LoginActivity, "Bentornato, ${utente.nome}!", Toast.LENGTH_SHORT).show()

                        // Navigazione verso la pagina principale
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)

                        // FLAG importanti: puliscono la cronologia così l'utente non torna al login col tasto indietro
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        // Se l'utente è null, le credenziali sono sbagliate
                        Toast.makeText(this@LoginActivity, "Codice Fiscale o Password errati", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // 2. COLLEGAMENTO ALLA PAGINA DI REGISTRAZIONE
        binding.tvVaiARegistrati.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            // Nota: non usiamo finish() perché l'utente potrebbe voler tornare indietro al login
        }

        // 3. TASTO PER TORNARE ALLA WELCOME
        binding.tvTornaIndietro.setOnClickListener {
            // Semplicemente chiudiamo questa Activity per tornare a quella precedente (Welcome)
            finish()
        }
    }
}