package com.mihaialexandruteodor406.unibuc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val logoutButton = findViewById<Button>(R.id.logout)

        logoutButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Logout successful!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MainActivity, StartActivity::class.java)
            startActivity(intent)
        }
    }
}