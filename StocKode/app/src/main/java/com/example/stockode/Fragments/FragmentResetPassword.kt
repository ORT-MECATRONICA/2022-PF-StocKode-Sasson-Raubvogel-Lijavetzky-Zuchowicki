package com.example.stockode.Fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import com.example.stockode.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class FragmentResetPassword : Fragment() {

    companion object {
        fun newInstance() = FragmentResetPassword()
    }

    private lateinit var viewModel: FragmentResetPasswordViewModel

    private lateinit var v: View
    private lateinit var EmailReset: EditText
    private lateinit var ButtonReset: Button
    private lateinit var ProgressBar: ProgressBar

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_fragment_reset_password, container, false)
        EmailReset = v.findViewById(R.id.editTextEmailResetPass)
        ButtonReset = v.findViewById(R.id.BtnReset)
        firebaseAuth = FirebaseAuth.getInstance()
        ProgressBar = v.findViewById(R.id.progressBarResetPass)
        return v
    }

    override fun onStart() {
        super.onStart()
        ButtonReset.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword() {
        var Email = EmailReset.text.toString().trim()

        if (Email.isNotEmpty()){
            if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
                EmailReset.setError("Invalid E-mail adress")
                EmailReset.requestFocus()
            }
            else {
                ProgressBar.visibility = View.VISIBLE
                firebaseAuth.sendPasswordResetEmail(Email).addOnCompleteListener {
                    if (it.isSuccessful){
                        Snackbar.make(v,"Mail sent to " + Email, Snackbar.LENGTH_SHORT).show()
                        ProgressBar.visibility = View.INVISIBLE
                    }
                    else {
                        Snackbar.make(v,"Something went wrong, try again", Snackbar.LENGTH_SHORT).show()
                        ProgressBar.visibility = View.INVISIBLE
                    }
                }
            }
        }
        else{
            EmailReset.setError("Empty field")
            EmailReset.requestFocus()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FragmentResetPasswordViewModel::class.java)
        // TODO: Use the ViewModel
    }
}
