package com.example.stockode.Fragments

import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import com.example.stockode.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class FragmentIngreso : Fragment() {

    companion object {
        fun newInstance() = FragmentIngreso()
    }

    private lateinit var viewModel: FragmentIngresoViewModel

    private lateinit var v:View
    private var db = Firebase.firestore

    private lateinit var cantidadIng: EditText
    private lateinit var btnIngresar: Button
    private lateinit var textCant: TextView
    private lateinit var imgProductoIng: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.ingreso_fragment, container, false)
        cantidadIng = v.findViewById(R.id.editTextCantidadIng)
        btnIngresar = v.findViewById(R.id.btnIngresar)
        textCant = v.findViewById(R.id.CantStock1)
        imgProductoIng = v.findViewById(R.id.imgProductoIng)
        return v
    }

    override fun onStart() {
        super.onStart()
        val producto = FragmentIngresoArgs.fromBundle(requireArguments()).producto
        val cantidad = FragmentIngresoArgs.fromBundle(requireArguments()).cantidad.toInt()
        textCant.text = cantidad.toString()

        val imageName = FragmentIngresoArgs.fromBundle(requireArguments()).nombreImg
        val StorageRef = FirebaseStorage.getInstance().reference.child("images/$imageName")

        val localfile = File.createTempFile("tempImage", "jpg")
        StorageRef.getFile(localfile).addOnSuccessListener {

            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            imgProductoIng.setImageBitmap(bitmap)
        }


        btnIngresar.setOnClickListener {
            val valorIng = cantidadIng.text.toString().toInt()
            val nuevoValor = cantidad + valorIng
            if (cantidadIng.text.toString().isNotEmpty()) {
                db.collection("Productos").document(producto)
                    .update("description", nuevoValor.toString())
                val action = FragmentIngresoDirections.actionIngresoToStock()
                v.findNavController().navigate(action)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FragmentIngresoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}