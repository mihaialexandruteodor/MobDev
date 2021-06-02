package com.mihaialexandruteodor406.unibucnavigator.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.mihaialexandruteodor406.unibucnavigator.R

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.login)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)

        loginButton.setOnClickListener{
            val txtEmail = email.text.toString()
            val txtPassword = password.text.toString()
            loginUser( txtEmail, txtPassword)
        }
    }

    private fun loginUser(txtEmail: String, txtPassword: String) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(txtEmail, txtPassword).addOnSuccessListener {
            Toast.makeText(this, "Login cu succes!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@Login, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}