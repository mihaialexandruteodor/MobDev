package com.mihaialexandruteodor406.unibuc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import com.mihaialexandruteodor406.unibuc.model.CourseDetails
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val logoutButton = findViewById<Button>(R.id.logout)
        val addCourse = findViewById<Button>(R.id.add)
        val name = findViewById<EditText>(R.id.name)
        val courseList = findViewById<ListView>(R.id.courseList)

        val gson = Gson()

        var courseData = ArrayList<String>()
        var courseDetailsList = ArrayList<CourseDetails>()
        var arrayAdapter = ArrayAdapter<String>(this, R.layout.list_item, courseData)

        courseList.adapter = arrayAdapter

        val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("Unibuc")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                courseData.clear()
                for(snapshot in dataSnapshot.getChildren())
                {
                    var jsonString = snapshot.child("Curs").value.toString()
                    var curs = gson.fromJson(jsonString, CourseDetails::class.java)
                    courseData.add(curs.courseName)
                    courseDetailsList.add(curs)
                }
                arrayAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w( "Failed to read value.", error.toException())
            }})

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
                FirebaseDatabase.getInstance().getReference().child("Unibuc")
                    .push().child("Curs").setValue(gson.toJson(CourseDetails(txtName)))
                Toast.makeText(this, "Course added!", Toast.LENGTH_SHORT).show()
                name.text.clear()
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