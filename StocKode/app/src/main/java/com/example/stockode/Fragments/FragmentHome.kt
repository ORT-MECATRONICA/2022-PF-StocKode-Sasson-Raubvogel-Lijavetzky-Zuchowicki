package com.example.stockode.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.stockode.R
import com.example.stockode.entities.Producto
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import kotlin.properties.Delegates

class FragmentHome : Fragment() {

    private lateinit var v: View
    private lateinit var btnStock: ImageView
    private lateinit var btnScanner: ImageView

    val db = Firebase.firestore
    var lista: MutableList<Producto> = ArrayList()

    private lateinit var viewModel: FragmentHomeViewModel

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
            .setBeepEnabled(false)
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
                viewModel.detectado = true
                viewModel.lectura = result.contents.toInt()
            } else {
                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onStart() {
        super.onStart()
        Snackbar.make(v,viewModel.lectura.toString(),Snackbar.LENGTH_SHORT).show()
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

        if(viewModel.detectado == true){
            viewModel.detectado = false
            val actionHomeToInformacion =
                FragmentHomeDirections.actionFragmentHomeToInformacion(
                    lista[viewModel.lectura].title,
                    lista[viewModel.lectura].description,
                    lista[viewModel.lectura].NombreImg,
                    lista[viewModel.lectura].numero
                )
            v.findNavController().navigate(actionHomeToInformacion)
            Snackbar.make(v,viewModel.lectura,Snackbar.LENGTH_SHORT).show()
        }
        btnScanner.setOnClickListener {
            setUpQrCode()
        }

        btnStock.setOnClickListener {
            val actionHomeStock = FragmentHomeDirections.actionFragmentHomeToStock()
            v.findNavController().navigate(actionHomeStock)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FragmentHomeViewModel::class.java)
    }

}