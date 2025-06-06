package com.grocery.groceryhub

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.grocery.groceryhub.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityLoginBinding
    var new = StartNewActivity()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        binding.createAccount.setOnClickListener{
            new.startingActivity(this@LoginActivity, SignupActivity::class.java)
        }
        loginUser()
    }

    private fun loginUser() {
        binding.login.setOnClickListener{
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()
            if (listOf(email, password).any { it.isEmpty() })  //if(name.isEmpty() || email.isEmpty() || password.isEmpty())
                Toast.makeText(this, "One of the required fields is empty", Toast.LENGTH_SHORT).show()
            else if(!isEmailValid(email))
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            else{
                signinUser(email, password)
            }
            //Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signinUser(email: String, password: String) {
        binding.loginProgressbar.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    binding.loginProgressbar.visibility = View.GONE
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    new.startingActivity(this@LoginActivity, OpeningActivity::class.java)
                } else {
                    binding.loginProgressbar.visibility = View.GONE
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext,"Authentication failed.", Toast.LENGTH_SHORT,).show()
                }
            }
    }

    private fun isEmailValid(email: String): Boolean {
        val commonDomains = listOf("gmail.com", "yahoo.com", "outlook.com", "hotmail.com")
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) return false

        val domain = email.substringAfter("@").lowercase()
        return domain in commonDomains
    }
}