package com.mihaialexandruteodor406.unibuc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val logoutButton = findViewById<Button>(R.id.logout)
        val addCourse = findViewById<Button>(R.id.add)
        val name = findViewById<EditText>(R.id.name)

        logoutButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Logout successful!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MainActivity, StartActivity::class.java)
            startActivity(intent)
        }

        addCourse.setOnClickListener {
            val txtName = name.text.toString()
            if(txtName.isEmpty())
            {
                Toast.makeText(this, "No course name entered!", Toast.LENGTH_SHORT).show()
            }
            else
            {
                FirebaseDatabase.getInstance().getReference().child("Unibuc").push().child("Curs").setValue(txtName)
                Toast.makeText(this, "Course added!", Toast.LENGTH_SHORT).show()
            }
        }


        //FirebaseDatabase.getInstance().getReference().child("Unibuc").child("Curs").setValue("Mobile Development")

       /* var map : HashMap<String, Any>
                = HashMap<String, Any> ()
        map.put("Name", "Teodor")
        map.put("Email", "alexandru.mihai2@s.unibuc.ro")

        FirebaseDatabase.getInstance().getReference().child("Unibuc").child("Users").updateChildren(map) */




    }
}