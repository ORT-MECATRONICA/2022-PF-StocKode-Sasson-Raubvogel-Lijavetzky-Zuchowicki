package com.example.stockode.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.stockode.R
import com.google.zxing.integration.android.IntentIntegrator

class FragmentScanner : Fragment() {

    lateinit var v: View


    private lateinit var viewModel: FragmentScannerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.scanner_fragment, container, false)
        return v;
    }

    private fun setUpQrCode() {
        IntentIntegrator(requireActivity())
            .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            .setTorchEnabled(false)
            .setBeepEnabled(true)
            .setPrompt("Scan QR Code")
            .initiateScan()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.contents != null) {
                // El contenido de cada escaneo se mostrará en un textView
                //Toast.makeText(requireContext(), result.contents, Toast.LENGTH_SHORT).show()
                //val actionScannerToInformacion = FragmentScannerDirections.actionScannerToInformacion()
                //v.findNavController().navigate(actionScannerToInformacion)
            } else {
                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onStart() {
        super.onStart()
        setUpQrCode()
    }
}