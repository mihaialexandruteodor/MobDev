package com.mihaialexandruteodor406.unibucnavigator.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.mihaialexandruteodor406.unibucnavigator.R
import com.mihaialexandruteodor406.unibucnavigator.ui.MainActivity.Singleton.descriereLocatii
import com.mihaialexandruteodor406.unibucnavigator.ui.MainActivity.Singleton.locatii
import kotlinx.android.synthetic.main.activity_detalii.view.*
import java.util.*
import kotlin.collections.ArrayList

class LocatiiAdapter():
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var locatiiFilterList = ArrayList<String>()
    lateinit var mcontext: Context

    class LocatiiHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    init {
        locatiiFilterList = locatii
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val locatiiListView =
                LayoutInflater.from(parent.context).inflate(R.layout.reciclerview_row, parent, false)
        val sch = LocatiiHolder(locatiiListView)
        mcontext = parent.context
        return sch
    }

    override fun getItemCount(): Int {
        return locatiiFilterList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // ...

        holder.itemView.detalii_text.text = locatiiFilterList[position]

        holder.itemView.setOnClickListener {
            val intent = Intent(mcontext, DetaliiActivity::class.java)
            intent.putExtra("selected", descriereLocatii[position])
            mcontext.startActivity(intent)
            Log.d("Selected:", locatiiFilterList[position])
        }

        // ...
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    locatiiFilterList = locatii
                } else {
                    val resultList = ArrayList<String>()
                    for (row in locatii) {
                        if (row.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    locatiiFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = locatiiFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                locatiiFilterList = results?.values as ArrayList<String>
                notifyDataSetChanged()
            }

        }
    }

}