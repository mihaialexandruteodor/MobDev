package com.mihaialexandruteodor406.unibuc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registerButton = findViewById<Button>(R.id.register)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)

        registerButton.setOnClickListener{
            val txtEmail = email.text.toString()
            val txtPassword = password.text.toString()

            if (TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)){
                Toast.makeText(this, "Empty credentials!", Toast.LENGTH_SHORT).show()
            }else if (txtPassword.length < 6)
            {
                Toast.makeText(this, "Password too short!", Toast.LENGTH_SHORT).show()
            }
            else
            {
                registerUser(txtEmail, txtPassword)
            }
        }
    }

    private fun registerUser(txtEmail: String, txtPassword: String) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(txtEmail, txtPassword)
            .addOnCompleteListener(this, OnCompleteListener { task ->
                if(task.isSuccessful)
                {
                    Toast.makeText(this, "Register successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Register, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    Toast.makeText(this, "Registration failed!", Toast.LENGTH_SHORT).show()
                }
            })
    }
}