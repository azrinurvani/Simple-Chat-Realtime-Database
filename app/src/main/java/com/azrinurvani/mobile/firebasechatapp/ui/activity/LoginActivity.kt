package com.azrinurvani.mobile.firebasechatapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.azrinurvani.mobile.firebasechatapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var auth : FirebaseAuth
    private var firebaseUser : FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
       auth.currentUser?.let {  firebaseUser = it }

        //check if user login then navigate to user screen
        if (firebaseUser!=null){
            val intent = Intent(this@LoginActivity, UserActivity::class.java)

            startActivity(intent)
            finish()
        }

        binding.apply {
            btnLogin.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
                    Toast.makeText(applicationContext,"Email and Password is required",Toast.LENGTH_LONG).show()
                }else{
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this@LoginActivity){ task ->
                        if (task.isSuccessful){
                            etEmail.setText("")
                            etPassword.setText("")
                            val intent = Intent(this@LoginActivity, UserActivity::class.java)

                            startActivity(intent)
                            finish()
                        }else{
                            Toast.makeText(applicationContext,"Email or password invalid",Toast.LENGTH_LONG).show()
                        }
                    }
                        .addOnFailureListener {
                            Toast.makeText(applicationContext,it.localizedMessage,Toast.LENGTH_LONG).show()
                        }
                }
            }

            btnSignUp.setOnClickListener {
                val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

}