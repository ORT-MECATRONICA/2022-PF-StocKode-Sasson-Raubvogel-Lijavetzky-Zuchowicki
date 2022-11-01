package com.example.stockode.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.stockode.R
import com.google.zxing.integration.android.IntentIntegrator

class FragmentHome : Fragment() {

    lateinit var v: View
    lateinit var btnStock: ImageView
    lateinit var btnScanner: ImageView
    lateinit var lectura: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_home_fragment, container, false)
        btnStock = v.findViewById(R.id.btnStock)
        btnScanner = v.findViewById(R.id.btnScanner)
        return v
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
                //val actionHomeToInformacion = FragmentHomeDirections.actionFragmentHomeToInformacion(result.contents)
                //v.findNavController().navigate(actionHomeToInformacion)
            } else {
                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
        lectura = result.contents
    }

    override fun onStart() {
        super.onStart()
        btnScanner.setOnClickListener {
            setUpQrCode()
        }

        btnStock.setOnClickListener {
            val actionHomeStock = FragmentHomeDirections.actionFragmentHomeToStock()
            v.findNavController().navigate(actionHomeStock)
        }
    }

}