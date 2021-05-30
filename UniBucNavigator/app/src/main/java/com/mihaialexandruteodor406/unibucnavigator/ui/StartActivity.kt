package com.mihaialexandruteodor406.unibucnavigator.ui;

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.mihaialexandruteodor406.unibucnavigator.R

class StartActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val registerButton = findViewById<Button>(R.id.register)
        val loginButton = findViewById<Button>(R.id.login)

        registerButton.setOnClickListener {
        val intent = Intent(this@StartActivity, Register::class.java)
        startActivity(intent)
        finish()
        }

        loginButton.setOnClickListener {
        val intent = Intent(this@StartActivity, Login::class.java)
        startActivity(intent)
        finish()
        }
        }
        }