package com.example.stockode

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import com.example.stockode.Fragments.FragmentInfromacion
import com.example.stockode.adapters.adapterProductos
import com.example.stockode.entities.Producto
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator

@Suppress("UNREACHABLE_CODE")
class LectorQR : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lector_qr)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED;

    }

    val db = Firebase.firestore
    var lista: MutableList<Producto> = ArrayList()

    override fun onStart() {
        super.onStart()

        setUpQrCode()

        var docRef = db.collection("Productos")

        docRef.get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot != null) {
                    for (document in dataSnapshot) {
                        val Recycler = Producto(
                            title = document.get("title") as String,
                            document.get("description") as String,
                            document.get("nombreImg") as String,
                            document.get("numero") as String
                        )
                        Log.d("Test", "DocumentSnapshot data: ${Recycler.toString()}")
                        lista.add(Recycler)
                    }
                } else {
                    Log.d("Test", "No such document")
                }
            }
        Toast.makeText(this,lista[0].NombreImg, Toast.LENGTH_SHORT).show()
    }
    private fun setUpQrCode() {
        IntentIntegrator(this)
            .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            .setTorchEnabled(false)
            .setBeepEnabled(false)
            .setPrompt("Scan QR Code")
            .setOrientationLocked(false)
            .initiateScan()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)

        if (result != null) {
            if (result.contents != null) {

            }else{
                Toast.makeText(this,"Cancelled", Toast.LENGTH_SHORT).show()
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
