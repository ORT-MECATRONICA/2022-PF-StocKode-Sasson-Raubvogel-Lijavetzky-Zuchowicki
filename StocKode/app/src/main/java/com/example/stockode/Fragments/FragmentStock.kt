package com.example.stockode.Fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stockode.R
import com.example.stockode.adapters.adapterProductos
import com.example.stockode.entities.Producto
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FragmentStock : Fragment() {

    private lateinit var v: View

    private var lista: MutableList<Producto> = ArrayList()

    private lateinit var RecyclerView: RecyclerView
    private lateinit var Btnmas: Button

    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.stock_fragment, container, false)
        RecyclerView = v.findViewById(R.id.recProductos)
        Btnmas = v.findViewById(R.id.btnMas)
        return v
    }

    override fun onStart() {
        super.onStart()
        lista.clear()

        Btnmas.setOnClickListener {
            val action = FragmentStockDirections.actionStockToFragmentAgregar()
            v.findNavController().navigate(action)
        }

        RecyclerView.setHasFixedSize(true)

        RecyclerView.layoutManager = LinearLayoutManager(context)

        RecyclerView.adapter = adapterProductos(lista, requireContext()) { index ->
            onItemClick(index)
        }

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
                    RecyclerView.adapter =
                        adapterProductos(lista, requireContext()) { pos -> onItemClick(pos) }


                } else {
                    Log.d("Test", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Test", "get failed with ", exception)
            }
    }

    fun onItemClick(pos: Int) {
        val action = FragmentStockDirections.actionStockToInformacion(
            lista[pos].title,
            lista[pos].description,
            lista[pos].NombreImg,
            lista[pos].numero
        )
        v.findNavController().navigate(action)
    }
}