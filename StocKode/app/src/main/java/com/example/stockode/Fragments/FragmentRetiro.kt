package com.example.stockode.Fragments

import android.graphics.BitmapFactory
import android.media.Image
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.findNavController
import com.example.stockode.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class FragmentRetiro : Fragment() {

    companion object {
        fun newInstance() = FragmentRetiro()
    }

    private lateinit var viewModel: FragmentRetiroViewModel
    private lateinit var v: View
    private lateinit var imgProducto: ImageView
    private lateinit var cantStock: TextView
    private lateinit var cantRetirada: EditText
    private lateinit var btnRetirar: Button

    private val db = Firebase.firestore
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.retiro_fragment, container, false)
        imgProducto = v.findViewById(R.id.imgProductoRet)
        cantRetirada = v.findViewById(R.id.editTextCantidadRet)
        cantStock = v.findViewById(R.id.CantStock2)
        btnRetirar = v.findViewById(R.id.btnRetirar)
        return v
    }

    override fun onStart() {
        super.onStart()

        val numero = FragmentIngresoArgs.fromBundle(requireArguments()).numero
        val producto = FragmentIngresoArgs.fromBundle(requireArguments()).producto
        val cantidad = FragmentIngresoArgs.fromBundle(requireArguments()).cantidad.toInt()
        cantStock.text = cantidad.toString()

        val imageName = FragmentIngresoArgs.fromBundle(requireArguments()).nombreImg
        val StorageRef = FirebaseStorage.getInstance().reference.child("images/$imageName")

        val localfile = File.createTempFile("tempImage", "jpg")
        StorageRef.getFile(localfile).addOnSuccessListener {

            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            imgProducto.setImageBitmap(bitmap)
        }

        btnRetirar.setOnClickListener {
            database = FirebaseDatabase.getInstance().getReference("test")
            database.child("flag").setValue(3).addOnSuccessListener {
                Snackbar.make(v, "Ponga los elementos en el lugar indicado", Snackbar.LENGTH_SHORT).show()
                database.child("int").setValue(numero.toInt())
            val cantidadRet = cantRetirada.text.toString()
            if (cantidadRet.isNotEmpty()){
                var resultado = cantidad - cantidadRet.toInt()
                if (resultado < 0) {
                    resultado = 0}
                db.collection("Productos").document(producto).update("description",resultado.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        val action = FragmentRetiroDirections.actionRetiroToStock()
                        v.findNavController().navigate(action)
                        }
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(),"Failed",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FragmentRetiroViewModel::class.java)
        // TODO: Use the ViewModel
    }

}