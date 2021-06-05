package com.mihaialexandruteodor406.unibucnavigator.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mihaialexandruteodor406.unibucnavigator.R
import kotlinx.android.synthetic.main.activity_detalii.*

class DetaliiActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalii)

        detalii_text.text = intent.extras!!.getString("selected")!!

    }
}