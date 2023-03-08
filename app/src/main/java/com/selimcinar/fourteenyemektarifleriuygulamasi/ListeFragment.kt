package com.selimcinar.fourteenyemektarifleriuygulamasi

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_liste.*


class ListeFragment : Fragment() {

    var yemekIsmiListesi = ArrayList<String>()
    var yemekIdListesi = ArrayList<Int>()
    private  lateinit var  listeAdapter:ListeRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_liste, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       //ListeAdapter ile listeler eklendi
        listeAdapter = ListeRecyclerAdapter(yemekIsmiListesi,yemekIdListesi)
        //Görsel yöneticisi ayarlanıyor
        recyclerView.layoutManager = LinearLayoutManager(context)
        //recyclerView adapter ile listeAdapter eşlendi
        recyclerView.adapter= listeAdapter
        //sqlVeriAlma fonksiyonu çalıştı
        sqlVeriAlma()
    }
    //Sqlden veri alma işlemi
    fun sqlVeriAlma(){
            try {
                //Activity üzerinden veri alındı
                activity?.let {
                    //database veri tabanı eklendi yada varolan açıldı
                    val database = it.openOrCreateDatabase("Yemekler",Context.MODE_PRIVATE,null)
                    //Veritabanı üzerinde sorgu işlemi yapıldı
                    val cursor = database.rawQuery("SELECT * FROM yemekler",null)
                    //Yemekisminin indexi alındı
                    val yemekIsmiIndex=cursor.getColumnIndex("yemekismi")
                    //Yemekidisinin indexi alındı
                    val yemekIdIndex = cursor.getColumnIndex("id")

                    //Listeler temizleniyor
                    yemekIsmiListesi.clear()
                    yemekIdListesi.clear()

                    //Veritabanı içinde dolaşacak
                    while (cursor.moveToNext()){
                        //yemekIsmiIndex üzerinden değerleri al
                        println(cursor.getString(yemekIsmiIndex))
                        //yemekIdIndex üzerinden değerleri al
                        println(cursor.getInt(yemekIdIndex))

                        //Listelere değerler ekleniyor
                        yemekIsmiListesi.add(cursor.getString(yemekIsmiIndex))
                        yemekIdListesi.add(cursor.getInt(yemekIdIndex))

                    }
                    listeAdapter.notifyDataSetChanged() // Değişen verileri ayarlayacka güncelleyecek
                    cursor.close()
                }
            }
            catch (e: Exception){
            e.printStackTrace()
            }
    }



}