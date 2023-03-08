package com.selimcinar.fourteenyemektarifleriuygulamasi

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_tarif.*
import java.io.ByteArrayOutputStream


class TarifFragment : Fragment() {
    var secilenGorsel: Uri? = null
    var secilenBitmap : Bitmap? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tarif, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Nesnelere içindeki onclick metotlarını fragmanlarda kullandırma
        button.setOnClickListener {
            kaydet(it)//kaydet onclikini fonksiyon oluşturup çağırdık
        }

        imageView.setOnClickListener {
            gorselSec(it)//gorselSec onclikini fonksiyon oluşturup çağırdık
        }
        arguments?.let {
            var gelenBilgi = TarifFragmentArgs.fromBundle(it).bilgi
            if (gelenBilgi.equals("MenudenGeldim")){
                //yeni bir yemek eklemeye geldi
                YemekİsimText.setText("")
                yemekMalzemeText.setText("")
                button.visibility=View.VISIBLE // button gorunur oldu

                val gorselSecmeArkaPlani = BitmapFactory.decodeResource(context?.resources,R.drawable.gorsel)
                imageView.setImageBitmap(gorselSecmeArkaPlani)
            }
            else{
                //daha önce oluşturlan yemeği görmeye geldi
                button.visibility = View.INVISIBLE //BUTTON GORUNMEZ OLDU
                //secilen yemegin datası gelir
                val secilenId = TarifFragmentArgs.fromBundle(it).id
                context?.let {
                    try {
                        val db = it.openOrCreateDatabase("Yemekler",Context.MODE_PRIVATE,null)
                        val cursor = db.rawQuery("Select * From yemekler Where id = ?", arrayOf(secilenId.toString()))

                        val yemekIsmiIndex = cursor.getColumnIndex("yemekismi")
                        val yemekMalzemeIndex =cursor.getColumnIndex("yemekmalzemesi")
                        val yemekGorseli = cursor.getColumnIndex("gorsel")

                        while (cursor.moveToNext()){
                            YemekİsimText.setText(cursor.getString(yemekIsmiIndex))
                            yemekMalzemeText.setText(cursor.getString(yemekMalzemeIndex))

                            val byteDizisi = cursor.getBlob(yemekGorseli)
                            val bitmap = BitmapFactory.decodeByteArray(byteDizisi,0,byteDizisi.size)
                            imageView.setImageBitmap(bitmap)
                        }
                        cursor.close()
                    }
                    catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
        }
    }   fun kaydet(view: View) {
        println("Tıklandı")
        //Sqlite'a kaydetme
        //Dışardan gelen değerleri değişkene kaydetme
        val yemekIsmi = YemekİsimText.text.toString()
        val yemekMalzemeleri = yemekMalzemeText.text.toString()
        //resim küçültme için geçerli kod
        if(secilenBitmap != null){
            val kucukuBitmap = kucukBitmapOlustur(secilenBitmap!!,300)
            //Veriyi diziye çevir , bitmapi veriye çevirme kodları
            val outputStream = ByteArrayOutputStream()
            kucukuBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteDizisi = outputStream.toByteArray()
            //Verileri SqlLite a kaydetme
            try {
                context?.let {  // Fragment olduğu için nullmı değilmi kontrol ediyoruz.Activiti olsa kontrol etmezdik
                    //Database veritabanı oluşturma
                    val database = it.openOrCreateDatabase("Yemekler", Context.MODE_PRIVATE,null)
                    //Veritabanına tablo oluşturma
                    database.execSQL("CREATE TABLE IF NOT EXISTS yemekler(id INTEGER PRIMARY KEY,yemekismi VARCHAR,yemekmalzemesi VARCHAR,gorsel BLOB)")
                    //Tabloya değerleri ekleme
                    val sqlString = "INSERT INTO yemekler(yemekismi,yemekmalzemesi,gorsel) VALUES (?,?,?)"
                    //Statement Stringleri sql kodu gibi çalıştır ve ? işareti olan yerlere değerler atar
                    val statement = database.compileStatement(sqlString)
                    statement.bindString(1,yemekIsmi) //1.indexe değer ekler
                    statement.bindString(2,yemekMalzemeleri)
                    statement.bindBlob(3,byteDizisi) //3.indexe görsel ekler
                    statement.execute()  //Sql kodlarını  çalıştırır.

                }

            }
            catch (e : Exception){
                e.printStackTrace()
            }
            //Sqle kayıt işi bittikten sonra listeye geri götüren kod.
            val action = TarifFragmentDirections.actionTarifFragmentToListeFragment()
            Navigation.findNavController(view).navigate(action)
        }
    }



    fun gorselSec(view: View) {
        println("Görsel tıklandı")
        //Görsel seçme işlemi
        //Görsel alma
        //Öncelikle  kullanıcıdan izin almak gerekli tehlikeli izinlerde
        //izin sormak için yazılan kodlar

        //let boş olabilir kontrolü
        activity?.let {
            if(ContextCompat.checkSelfPermission(it.applicationContext,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //PERMİSSİON_GRANTED İZİN VERİLDİ , PERMİSSİON_DENİED İZİN VERİLMEDİ
                //İzin verilmedi , izin istememiz gereken bloku
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)//Dizi içerisine birden çok izin eklenebilir
            }
            else{
                // izin zaten verilmiş , tekrar istemeden galeriye git bloku
                val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI) //Intent aksiyonlarından görsel al galeriye git
                startActivityForResult(galeriIntent,2)  //sonunda birşey dönecek demek
            }
        }



    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) { //Alınan izinler sonucunda neler olacak
        if (requestCode == 1){
            if(grantResults.size>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //İzini aldık ve galeriye gidecek kodu ekledik
                val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI) //Intent aksiyonlarından görsel al galeriye git
                startActivityForResult(galeriIntent,2)
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }
    //StartActivityResult Galeriye gidince ne olacak
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){ // cevap kodu 2 ise ve sonuç kodu Activity.RESULT_OK Tamamsa ve data verisi boş değilse çalışır.
            secilenGorsel = data.data // secilen gorsel telefonda nerede duruyor

            try {
                context?.let { //fragman içinde olduğumuz için let kullandık activityde olsak buna çok gerek yoktu
                    if (secilenGorsel != null){
                        if (Build.VERSION.SDK_INT>=28){
                            //Sdk 28 den buyukse bazı metotları kullandırır.Buyuk degılse farklı metotları kullandırır.
                            val source=  ImageDecoder.createSource(it.contentResolver,secilenGorsel!!)
                            secilenBitmap = ImageDecoder.decodeBitmap(source)
                            imageView.setImageBitmap(secilenBitmap)
                        }
                        else {
                            //Sdk 28 den kucukse bu kodlar çalışacak ve resme gidecek
                            secilenBitmap = MediaStore.Images.Media.getBitmap(it.contentResolver,secilenGorsel)
                            imageView.setImageBitmap(secilenBitmap)
                        }

                    }
                }


            }
            catch (e : Exception){
                e.printStackTrace()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
    fun kucukBitmapOlustur(kullanicininSectigiBitmap:Bitmap,maximumBoyut:Int) :Bitmap{ //Resimlerin boyutunu kapladığı alanı küçültme
        var width = kullanicininSectigiBitmap.width
        var height = kullanicininSectigiBitmap.height

        val bitmapOrani : Double = width.toDouble() / height.toDouble()

        if(bitmapOrani>1){
            //görselimiz yatay
            width = maximumBoyut
            val kisaltilmisHeight = width / bitmapOrani
            height = kisaltilmisHeight.toInt()
        }
        else{
            //görselimiz dikey
            height = maximumBoyut
            val kisaltilmisWidth = height + bitmapOrani
            width = kisaltilmisWidth.toInt()
        }

        return  Bitmap.createScaledBitmap(kullanicininSectigiBitmap,width,height,true)

    }

}
