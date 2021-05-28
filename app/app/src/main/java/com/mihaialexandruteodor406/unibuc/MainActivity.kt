package com.mihaialexandruteodor406.unibuc

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.*
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import com.mihaialexandruteodor406.unibuc.model.CourseDetails
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_unibuc
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


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
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_activity2, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}