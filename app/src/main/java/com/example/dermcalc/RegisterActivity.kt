package com.example.dermcalc

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dermcalc.controls.RLControls
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

        // Forza il maiuscolo mentre l'utente scrive nel campo CF
        binding.regCF.filters = arrayOf(InputFilter.AllCaps())

        binding.btnSalvaRegistrazione.setOnClickListener {
            val nome = binding.regNome.text.toString().trim()
            val cognome = binding.regCognome.text.toString().trim()
            val cf = binding.regCF.text.toString().trim().uppercase()
            val email = binding.regEmail.text.toString().trim()
            val pass = binding.regPass.text.toString().trim()

            // Validazioni (rimangono uguali, ottime!)
            if (nome.isEmpty() || cognome.isEmpty() || cf.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Tutti i campi sono obbligatori", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!RLControls.isCFValido(cf)) {
                binding.regCF.error = "Codice Fiscale non valido (16 caratteri richiesti)"
                return@setOnClickListener
            }

            if (!RLControls.isEmailValida(email)) {
                binding.regEmail.error = "Inserisci una mail valida"
                return@setOnClickListener
            }

            if (!RLControls.isPasswordSicura(pass)) {
                binding.regPass.error = "Password errata: deve essere lunga minimo 8 caratteri, serve 1 maiuscola, 1 numero e 1 simbolo[@#$%^&+=!_]"
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                // Controllo esistenza
                val utenteEsistente = db.dermCalcDao().checkUtenteEsistente(cf)

                withContext(Dispatchers.Main) {
                    if (utenteEsistente != null) {
                        binding.regCF.error = "Questo Codice Fiscale è già registrato!"
                        Toast.makeText(this@RegisterActivity, "Utente già presente", Toast.LENGTH_SHORT).show()
                    } else {
                        // Spostiamo tutto il salvataggio nell'ELSE
                        val passwordCriptata = RLControls.hashPassword(pass)
                        val nuovoUtente = Utente(
                            codFiscale = cf,
                            nome = nome,
                            cognome = cognome,
                            email = email,
                            password = passwordCriptata
                        )

                        // Eseguiamo l'insert in un thread secondario
                        launch(Dispatchers.IO) {
                            db.dermCalcDao().insertUtente(nuovoUtente)
                        }

                        Toast.makeText(this@RegisterActivity, "Registrazione completata!", Toast.LENGTH_SHORT).show()
                        finish() // Chiude solo se la registrazione ha successo
                    }
                }
            }
        }

        binding.tvTornaAlLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}