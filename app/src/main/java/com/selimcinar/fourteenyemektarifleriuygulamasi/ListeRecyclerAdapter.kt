package com.selimcinar.fourteenyemektarifleriuygulamasi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_row.view.*

class ListeRecyclerAdapter(private val yemekListesi: ArrayList<String>, val idListesi : ArrayList<Int>) : RecyclerView.Adapter<ListeRecyclerAdapter.YemekHolder>() {

        class  YemekHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YemekHolder {
        //Hangi rowu hangi tasarımla yapılacağını söyler
        val inflater = LayoutInflater.from(parent.context)
        val  view = inflater.inflate(R.layout.recycler_row,parent,false)
        return YemekHolder(view)
    }

    override fun getItemCount(): Int {
        //Kaç tane recycler row olsun
        return  yemekListesi.size
    }

    override fun onBindViewHolder(holder: YemekHolder, position: Int) {
        // görünümde ne olacak
        holder.itemView.recyclerView_row_text.text = yemekListesi.get(position)

        //resme tıklanınca ne olacak
        holder.itemView.setOnClickListener {
            val action = ListeFragmentDirections.actionListeFragmentToTarifFragment()
            Navigation.findNavController(it).navigate(action)

        }
    }


}