package com.example.stockode.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.findNavController
import com.example.stockode.R
import com.example.stockode.entities.Usuarios
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class FragmentInicio : Fragment() {

    lateinit var v: View

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var Email: EditText
    private lateinit var Password: EditText
    private lateinit var btnLogIn: Button
    private lateinit var txtNotRegistered: TextView
    private lateinit var txtForgotPassword: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_inicio_fragment, container, false)
        btnLogIn = v.findViewById(R.id.btnLogIn)
        Email = v.findViewById(R.id.editTextUser)
        Password = v.findViewById(R.id.editTextPassword)
        firebaseAuth = FirebaseAuth.getInstance()
        txtNotRegistered = v.findViewById(R.id.txtNoRegistrado)
        txtForgotPassword = v.findViewById(R.id.txtForgotPassword)
        return v
    }

    override fun onStart() {
        super.onStart()
        txtNotRegistered.setOnClickListener {
            val action = FragmentInicioDirections.actionFragmentInicioToFragmentRegister()
            v.findNavController().navigate(action)
        }

        txtForgotPassword.setOnClickListener {
            val action = FragmentInicioDirections.actionFragmentInicioToFragmentResetPassword()
            v.findNavController().navigate(action)
        }

        btnLogIn.setOnClickListener {
            val EmailIn = Email.text.toString().trim()
            val PasswordIn = Password.text.toString().trim()

            if (EmailIn.isNotEmpty() && PasswordIn.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(EmailIn, PasswordIn).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val action = FragmentInicioDirections.actionFragmentInicioToFragmentHome()
                        v.findNavController().navigate(action)
                    } else {
                        Snackbar.make(v, it.exception.toString(), Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
            else if (EmailIn.isEmpty()) {
                Email.setError("Empty field")
                Snackbar.make(v, "All fields must be completed", Snackbar.LENGTH_SHORT).show()
            }
            else if (PasswordIn.isEmpty()){
                Password.setError("Empty field")
                Snackbar.make(v, "All fields must be completed", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}