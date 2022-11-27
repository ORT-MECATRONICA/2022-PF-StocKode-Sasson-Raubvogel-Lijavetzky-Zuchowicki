package com.example.stockode.Fragments

import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.stockode.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class FragmentQR : Fragment() {

    companion object {
        fun newInstance() = FragmentQR()
    }

    private lateinit var viewModel: FragmentQRViewModel
    private lateinit var v: View

    private lateinit var nombreProducto: TextView
    private lateinit var qrProducto: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_fragment_q_r, container, false)
        nombreProducto = v.findViewById(R.id.txtNombreQrProducto)
        qrProducto = v.findViewById(R.id.imgQR)
        return v
    }

    override fun onStart() {
        super.onStart()
        val Nombre = FragmentQRArgs.fromBundle(requireArguments()).nombre
        nombreProducto.text = "QR del producto: " + Nombre

        var imageName = "QR " + Nombre
        val StorageRef = FirebaseStorage.getInstance().reference.child("QR/$imageName")

        val localfile = File.createTempFile("tempImage", "jpg")
        StorageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            qrProducto.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Snackbar.make(v,it.toString(), Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FragmentQRViewModel::class.java)
        // TODO: Use the ViewModel
    }

}