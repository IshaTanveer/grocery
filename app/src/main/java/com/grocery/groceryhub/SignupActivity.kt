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
import com.grocery.groceryhub.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private  lateinit var binding: ActivitySignupBinding
    var new = StartNewActivity()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        binding.loginHere.setOnClickListener{
            new.startingActivity(this@SignupActivity, LoginActivity::class.java)
        }
        signupFun()
    }

    private fun signupFun() {
        binding.signup.setOnClickListener{
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()

            if (listOf(email, password, confirmPassword).any { it.isEmpty() })  //if(name.isEmpty() || email.isEmpty() || password.isEmpty())
                Toast.makeText(this, "One of the required fields is empty", Toast.LENGTH_SHORT).show()
            else{
                if(!isEmailValid(email))
                    Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                else if(!isStrongPassword(password))
                    Toast.makeText(this, "Password must be at least 8 characters and include a special character", Toast.LENGTH_SHORT).show()
                else if(password != confirmPassword)
                    Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
                else{
                    makeNewUser(email, password)
                }
            }

        }
    }

    private fun makeNewUser(email: String, password: String) {
        binding.signupProgressbar.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    binding.signupProgressbar.visibility = View.GONE
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    new.startingActivity(this@SignupActivity, LoginActivity::class.java)
                } else {
                    binding.signupProgressbar.visibility = View.GONE
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT,).show()
                }
            }

    }

    private fun isEmailValid(email: String): Boolean {
        val commonDomains = listOf("gmail.com", "yahoo.com", "outlook.com", "hotmail.com")
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) return false

        val domain = email.substringAfter("@").lowercase()
        return domain in commonDomains
    }

    private fun isStrongPassword(password: String): Boolean {
        val passwordPattern = Regex("^(?=.*[!@#\$%^&*()_+=\\-{}\\[\\]:;\"'<>,.?/]).{8,}$")
        return password.matches(passwordPattern)
    }

}