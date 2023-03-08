package com.selimcinar.fourteenyemektarifleriuygulamasi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //menuyu bağlama layouta
        val menuInflater = menuInflater
        //yemeklistesi_ekle xml,menuye eklendi.
        menuInflater.inflate(R.menu.yemeklistesi_ekle,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Menuden herhangi bir item seçenek seçilirse ne olacak giriş,çıkış ayarlar vs.
        if (item.itemId == R.id.yemekekle_item){
            //basılan itemın idsi kontrol ediliyor.
            //aksiyon ile tarifFragmente gider
            val action = ListeFragmentDirections.actionListeFragmentToTarifFragment()
            //(this,navhostunid adı).navigate(nereye gidecek)
            Navigation.findNavController(this,R.id.fragment).navigate(action)

        }
        return super.onOptionsItemSelected(item)
    }

}
