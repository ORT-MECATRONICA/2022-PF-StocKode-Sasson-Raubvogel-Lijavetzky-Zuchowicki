package com.example.stockode.Fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.stockode.R
import com.example.stockode.entities.Producto
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class FragmentAgregar : Fragment() {

    companion object {
        fun newInstance() = FragmentAgregar()
    }

    private lateinit var viewModel: FragmentAgregarViewModel
    lateinit var v: View

    val db = Firebase.firestore

    var lista: MutableList<Producto> = ArrayList()

    private lateinit var ImageUri: Uri

    private var cantidad: Int = 0
    private lateinit var Objeto: EditText
    private lateinit var Descripcion: EditText
    private lateinit var btnCrearClase: Button
    private lateinit var btnSeleccionar: Button
    private lateinit var imagenSeleccionada: ImageView

    private var Seleccionado: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_fragment_agregar, container, false)
        Objeto = v.findViewById(R.id.NombreObjeto)
        Descripcion = v.findViewById(R.id.DescripcionObjeto)
        btnCrearClase = v.findViewById(R.id.BtnCrearObjeto)
        btnSeleccionar = v.findViewById(R.id.btnSeleccionar)
        imagenSeleccionada = v.findViewById(R.id.imagenSeleccionada)
        return v
    }

    override fun onStart() {
        super.onStart()
        btnSeleccionar.setOnClickListener {
            Select()
        }
        btnCrearClase.setOnClickListener {

            db.collection("productos").get().addOnSuccessListener { cantidadProducos->
                for (documents in cantidadProducos){
                    cantidad = cantidad + 1
                }
            }
            if (Objeto.text.toString().isNotEmpty() && Descripcion.text.toString().isNotEmpty() && Seleccionado) {
                Upload()
                var objeto = hashMapOf(
                    "title" to Objeto.text.toString().trim(),
                    "description" to Descripcion.text.toString().trim(),
                    "nombreImg" to "imagen " + Objeto.text.toString().trim(),
                    "numero" to cantidad
                )
                db.collection("Productos").document(Objeto.text.toString().trim())
                    .set(objeto)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                            val actionVolverStock =
                                FragmentAgregarDirections.actionFragmentAgregarToStock()
                            v.findNavController().navigate(actionVolverStock)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.d(ContentValues.TAG, "Error writing document", e)
                    }
            }
            if (Objeto.text.toString().isEmpty()) {
                Objeto.setError("Campo vacío")
            }
            if (Descripcion.text.toString().isEmpty()) {
                Descripcion.setError("Campo vacío")
            }
        }
    }

    private fun Select() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {

            ImageUri = data?.data!!
            imagenSeleccionada.setImageURI(ImageUri)
            Seleccionado = true

        }
    }

    private fun Upload() {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Uploading File...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val fileName = "imagen " + Objeto.text.toString().trim()
        val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

        storageReference.putFile(ImageUri).addOnSuccessListener {

            imagenSeleccionada.setImageURI(null)
            Toast.makeText(requireContext(), "Successfuly upload", Toast.LENGTH_SHORT).show()
            if (progressDialog.isShowing) progressDialog.dismiss()
            Seleccionado = false


        }.addOnFailureListener {

            if (progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(requireContext(), "Failed ", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FragmentAgregarViewModel::class.java)
        // TODO: Use the ViewModel
    }

}