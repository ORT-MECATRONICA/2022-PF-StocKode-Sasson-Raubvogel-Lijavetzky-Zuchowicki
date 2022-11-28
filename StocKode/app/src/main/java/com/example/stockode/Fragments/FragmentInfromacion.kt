package com.example.stockode.Fragments

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.app.DownloadManager
import android.app.DownloadManager.Request
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.stockode.R
import com.example.stockode.entities.Producto
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.jar.Manifest


class FragmentInfromacion : Fragment() {

    private lateinit var v: View
    private lateinit var btnIrAIngreso: Button
    private lateinit var btnIrARetiro: Button
    private lateinit var txtProducto: TextView
    private lateinit var txtCantidad: TextView
    private lateinit var builder: AlertDialog.Builder
    private lateinit var imagen: ImageView
    private lateinit var Borrar: ImageView
    private lateinit var descargarQR: TextView

    private var db = Firebase.firestore

    private var lista: MutableList<Producto> = ArrayList()
    private var i: Int = -1

    private var dataUrl: String = ""
    private val REQ_CODE = 100


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.informacion_fragment, container, false)
        btnIrAIngreso = v.findViewById(R.id.btnIrAIngreso)
        btnIrARetiro = v.findViewById(R.id.btnIrARetiro)
        txtProducto = v.findViewById(R.id.TxtProducto)
        txtCantidad = v.findViewById(R.id.NumCant)
        imagen = v.findViewById(R.id.imagenProducto)
        Borrar = v.findViewById(R.id.btnEliminar)
        builder = AlertDialog.Builder(requireContext())
        descargarQR = v.findViewById(R.id.descargarQR)
        return v
    }

    override fun onStart() {
        super.onStart()

        var manager: DownloadManager

        var numero = FragmentInfromacionArgs.fromBundle(requireArguments()).numero

        val Nombre = FragmentInfromacionArgs.fromBundle(requireArguments()).title
        txtProducto.text = Nombre

        val Cantidad = FragmentInfromacionArgs.fromBundle(requireArguments()).description
        txtCantidad.text = Cantidad

        var imageName = FragmentInfromacionArgs.fromBundle(requireArguments()).nombreImg
        val StorageRef = FirebaseStorage.getInstance().reference.child("images/$imageName")

        val localfile = File.createTempFile("tempImage", "jpg")
        StorageRef.getFile(localfile).addOnSuccessListener {

            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            imagen.setImageBitmap(bitmap)

        }.addOnFailureListener {
        }

        descargarQR.setOnClickListener {
            val action = FragmentInfromacionDirections.actionInformacionToFragmentQR(Nombre, numero)
            v.findNavController().navigate(action)
        }

        btnIrAIngreso.setOnClickListener {
            val actionInfoIngreso = FragmentInfromacionDirections.actionInformacionToIngreso(
                imageName,
                Cantidad,
                Nombre,
                numero
            )
            v.findNavController().navigate(actionInfoIngreso)
        }

        btnIrARetiro.setOnClickListener {
            val actionInfoRetiro = FragmentInfromacionDirections.actionInformacionToRetiro(
                imageName,
                Cantidad,
                Nombre,
                numero
            )
            v.findNavController().navigate(actionInfoRetiro)
        }
        Borrar.setOnClickListener {
            builder.setTitle("Borrar elemento")
                .setMessage("¿Seguro que desea eliminar este producto?")
                .setCancelable(true)
                .setPositiveButton("Sí") { dialogInterface, it ->
                    db.collection("Productos").document(Nombre).delete()
                    val action = FragmentInfromacionDirections.actionInformacionToStock()
                    v.findNavController().navigate(action)
                    val fileName = "imagen " + Nombre
                    val storageReference =
                        FirebaseStorage.getInstance().getReference("images/$fileName")
                    storageReference.delete()
                }
                .setNegativeButton("No") { dialogInterface, It ->
                    dialogInterface.cancel()
                }.show()
        }
    }
}