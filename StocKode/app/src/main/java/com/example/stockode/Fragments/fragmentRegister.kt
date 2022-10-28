package com.example.stockode.Fragments

import androidx.lifecycle.ViewModelProvider
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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class fragmentRegister : Fragment() {

    companion object {
        fun newInstance() = fragmentRegister()
    }

    lateinit var v: View
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var Email: EditText
    private lateinit var Password: EditText
    private lateinit var RepeatPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var txtAlreadyRegistered: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_register_fragment, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        Email = v.findViewById(R.id.editTextRegisterEmail)
        Password = v.findViewById(R.id.editTextRegisterPassword)
        RepeatPassword = v.findViewById(R.id.editTextRepeatPassword)
        btnSignUp = v.findViewById(R.id.btnSignUp)
        txtAlreadyRegistered = v.findViewById(R.id.txtYaRegistrado)
        return v
    }

    override fun onStart() {
        super.onStart()

        txtAlreadyRegistered.setOnClickListener {
            val action = fragmentRegisterDirections.actionFragmentRegisterToFragmentInicio()
            v.findNavController().navigate(action)
        }
        btnSignUp.setOnClickListener{
            val EmailIn = Email.text.toString().trim()
            val PasswordIn = Password.text.toString().trim()
            val RepeatPasswordIn = RepeatPassword.text.toString().trim()

            if (EmailIn.isNotEmpty() && PasswordIn.isNotEmpty() && RepeatPasswordIn.isNotEmpty()){
                if (PasswordIn == RepeatPasswordIn){
                    firebaseAuth.createUserWithEmailAndPassword(EmailIn,PasswordIn).addOnCompleteListener{
                        if (it.isSuccessful){
                            Snackbar.make(v, "User signed up successfuly", Snackbar.LENGTH_SHORT).show()
                            val action = fragmentRegisterDirections.actionFragmentRegisterToFragmentInicio()
                            v.findNavController().navigate(action)
                        }
                        else{
                            Snackbar.make(v,it.exception.toString(), Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Snackbar.make(v, "Your password does not match", Snackbar.LENGTH_SHORT).show()
                }
            }
            else if (EmailIn.isEmpty()) {
                Email.setError("Empty field")
                Snackbar.make(v, "All fields must be completed", Snackbar.LENGTH_SHORT).show()
            }
            else if (PasswordIn.isEmpty()) {
                Password.setError("Empty field")
                Snackbar.make(v, "All fields must be completed", Snackbar.LENGTH_SHORT).show()
            }
            else if (RepeatPasswordIn.isEmpty()) {
                RepeatPassword.setError("Empty field")
                Snackbar.make(v, "All fields must be completed", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

}