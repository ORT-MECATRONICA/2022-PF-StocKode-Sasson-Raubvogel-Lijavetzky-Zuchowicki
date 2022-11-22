package com.example.stockode.Fragments

import android.R.attr
import android.R.attr.bitmap
import android.app.AlertDialog
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.stockode.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.ByteArrayOutputStream
import java.io.File


class FragmentInfromacion : Fragment() {

    private lateinit var v: View
    private lateinit var btnIrAIngreso: Button
    private lateinit var btnIrARetiro: Button
    private lateinit var txtProducto: TextView
    private lateinit var txtCantidad: TextView
    private lateinit var builder: AlertDialog.Builder
    private lateinit var imagen: ImageView
    private lateinit var Borrar: ImageView
    private lateinit var descargar: TextView

    private var Nombre = FragmentInfromacionArgs.fromBundle(requireArguments()).title
    private var Cantidad = FragmentInfromacionArgs.fromBundle(requireArguments()).description
    private var Orden = FragmentInfromacionArgs.fromBundle(requireArguments()).orden

    private lateinit var ImageUri: Uri

    private var db = Firebase.firestore

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
        descargar = v.findViewById(R.id.descargarQR)
        return v
    }

    override fun onStart() {
        super.onStart()

        txtProducto.text = Nombre
        txtCantidad.text = Cantidad

        var imageName = FragmentInfromacionArgs.fromBundle(requireArguments()).nombreImg
        val StorageRef = FirebaseStorage.getInstance().reference.child("images/$imageName")

        val localfile = File.createTempFile("tempImage", "jpg")
        StorageRef.getFile(localfile).addOnSuccessListener {

            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            imagen.setImageBitmap(bitmap)

        }.addOnFailureListener {
        }

        descargar.setOnClickListener {
            try {
                var barcodeEncoder: BarcodeEncoder = BarcodeEncoder()
                var bitmap: Bitmap = barcodeEncoder.encodeBitmap(
                    Orden,
                    BarcodeFormat.QR_CODE,
                    300,
                    300
                )
                //codigoQr.setImageBitmap(bitmap)
                val fileName = "QR " + Nombre
                val storageReference = FirebaseStorage.getInstance().getReference("QR/$fileName")

                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
                val image: ByteArray = stream.toByteArray()

                storageReference.putBytes(image).addOnSuccessListener {
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        btnIrAIngreso.setOnClickListener {
            val actionInfoIngreso = FragmentInfromacionDirections.actionInformacionToIngreso(
                imageName,
                Cantidad,
                Nombre
            )
            v.findNavController().navigate(actionInfoIngreso)
        }

        btnIrARetiro.setOnClickListener {
            val actionInfoRetiro =
                FragmentInfromacionDirections.actionInformacionToRetiro(imageName, Cantidad, Nombre)
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