package com.example.dermcalc.controls

import java.security.MessageDigest
/**
 * Singleton che raccoglie le utility di validazione e sicurezza per l'applicazione.
 * Gestisce la logica di controllo per input utente e la crittografia delle password.
 */
object RLControls {

    /**
     * Verifica se la stringa fornita segue il formato standard di un indirizzo email.
     * Utilizza i pattern predefiniti di Android per una validazione affidabile.
     */
    fun isEmailValida(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Valida il Codice Fiscale.
     * Il formato richiesto è quello standard da 16 caratteri alfanumerici:
     * - 6 lettere (Cognome/Nome)
     * - 2 numeri (Anno)
     * - 1 lettera (Mese)
     * - 2 numeri (Giorno/Sesso)
     * - 1 lettera (Comune)
     * - 3 numeri (Codice catastale)
     * - 1 lettera (Carattere di controllo)
     */
    fun isCFValido(cf: String): Boolean {
        val regexCF = Regex("^[A-Z]{6}[0-9]{2}[A-Z][0-9]{2}[A-Z][0-9]{3}[A-Z]$")
        return cf.matches(regexCF)
    }

    /**
     * Valida il livello di sicurezza della password.
     * Requisiti minimi:
     * - Almeno 8 caratteri
     * - Almeno una lettera maiuscola
     * - Almeno un numero
     * - Almeno un carattere speciale tra quelli definiti [@#$%^&+=!_]
     * - Nessuno spazio vuoto consentito
     */
    fun isPasswordSicura(psw: String): Boolean {
        // Almeno 8 caratteri, una maiuscola, un numero e un carattere speciale
        val regexPsw = Regex("^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!_])(?=\\S+$).{8,}$")
        return psw.matches(regexPsw)
    }

    /**
     * Genera un hash sicuro della password utilizzando l'algoritmo SHA-256.
     * La password viene convertita in una stringa esadecimale.
     * @param password La stringa in chiaro da criptare.
     * @return La stringa hashata pronta per il salvataggio nel database.
     */
    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

}