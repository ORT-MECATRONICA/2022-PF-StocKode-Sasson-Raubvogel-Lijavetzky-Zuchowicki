package com.example.stockode.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.BitmapCompat
import androidx.core.graphics.createBitmap
import androidx.recyclerview.widget.RecyclerView
import com.example.stockode.R
import com.example.stockode.entities.Producto
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class adapterProductos(
    var lista: MutableList<Producto>,
    var context: Context,
    val onItemClick : (Int) -> Unit
) : RecyclerView.Adapter<adapterProductos.Holder>(){

    class Holder (v: View) : RecyclerView.ViewHolder(v){
        private var view: View
        init {
            this.view = v
        }
        fun setTitle (title : String){
            var txtTitle : TextView = view.findViewById(R.id.txtNombreProducto)
            txtTitle.text = title
        }

        fun getImageView (urlImage: String) : ImageView {
            return view.findViewById(R.id.imagenProducto)
        }
        fun getCardView () : CardView {
            return view.findViewById(R.id.cardProductos)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_productos,parent,false)
        return (Holder(view))
    }

    override fun onBindViewHolder(holder: Holder, pos: Int) {
        holder.setTitle(lista[pos].title)
        fun setData(newData: ArrayList<Producto>){
            this.lista = newData
            this.notifyDataSetChanged()
        }

        val imageName = lista[pos].NombreImg
        val StorageRef = FirebaseStorage.getInstance().reference.child("images/$imageName")

        val localfile = File.createTempFile("tempImage", "jpg")
        StorageRef.getFile(localfile).addOnSuccessListener {

            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            val bitmapScaled = Bitmap.createScaledBitmap(bitmap, 1760, 1920, true)
            holder.getImageView(lista[pos].NombreImg).setImageBitmap(bitmapScaled)

        }.addOnFailureListener{
        }

        holder.getCardView().setOnClickListener  () {
            onItemClick(pos)
        }

    }

    override fun getItemCount(): Int {
        return lista.size
    }
}