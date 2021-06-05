package com.mihaialexandruteodor406.unibucnavigator.ui

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mihaialexandruteodor406.unibucnavigator.R
import com.mihaialexandruteodor406.unibucnavigator.ui.MainActivity.Singleton.descriereLocatii
import com.mihaialexandruteodor406.unibucnavigator.ui.MainActivity.Singleton.locatii
import kotlinx.android.synthetic.main.activity_locatii.*

class LocatiiActivity() : AppCompatActivity() {

    lateinit var adapter: LocatiiAdapter
    lateinit var locatiirv: RecyclerView




    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locatii)

        locatiirv = findViewById(R.id.unibuc_list)
        locatiirv.layoutManager = LinearLayoutManager(locatiirv.context)
        locatiirv.setHasFixedSize(true)

        unibuc_search.setOnQueryTextListener(object: SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })


        adapter = LocatiiAdapter()
        locatiirv.adapter = adapter
    }



}

