package com.example.stockode

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.zxing.integration.android.IntentIntegrator

class LectorQR : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lector_qr)

        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    }

    override fun onStart() {
        super.onStart()
        setUpQrCode()
    }
    private fun setUpQrCode() {
        IntentIntegrator(this)
            .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            .setTorchEnabled(false)
            .setBeepEnabled(true)
            .setPrompt("Scan QR Code")
            .setOrientationLocked(true)
            .initiateScan()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)

        if (result != null) {
            if (result.contents != null) {
                // El contenido de cada escaneo se mostrará en un textView
                //Toast.makeText(this, result.contents, Toast.LENGTH_SHORT).show()//binding.textView.text = result.contents
               // val actionLectorQRInformacion = LectorQRDirections.actionLectorQRToInformacion()
                //this.findNavController().navigate(actionLectorQRInformacion)
            }else{
                Toast.makeText(this,"Cancelled", Toast.LENGTH_SHORT).show()
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
