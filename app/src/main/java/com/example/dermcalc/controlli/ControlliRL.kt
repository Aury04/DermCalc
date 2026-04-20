package com.example.dermcalc.controlli

import java.security.MessageDigest

object ControlliRL {

    // Funzione per validare la Email
    fun isEmailValida(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Funzione per validare il Codice Fiscale (16 caratteri: 3L 3L 2N 1L 2N 1L 3N 1L)
    fun isCFValido(cf: String): Boolean {
        val regexCF = Regex("^[A-Z]{6}[0-9]{2}[A-Z][0-9]{2}[A-Z][0-9]{3}[A-Z]$")
        return cf.matches(regexCF)
    }

    // Funzione per validare la Password
    fun isPasswordSicura(psw: String): Boolean {
        // Almeno 8 caratteri, una maiuscola, un numero e un carattere speciale
        val regexPsw = Regex("^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!_])(?=\\S+$).{8,}$")
        return psw.matches(regexPsw)
    }

    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

}