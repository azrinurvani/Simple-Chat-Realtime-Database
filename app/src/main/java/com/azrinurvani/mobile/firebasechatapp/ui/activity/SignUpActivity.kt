package com.azrinurvani.mobile.firebasechatapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.azrinurvani.mobile.firebasechatapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySignUpBinding

    private lateinit var auth : FirebaseAuth
    private lateinit var databaseReference : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.apply {
            btnSignUp.setOnClickListener {
                val userName = etName.text.toString()
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                val confirmPassword = etConfirmPassword.text.toString()

                if (TextUtils.isEmpty(userName)){
                    Toast.makeText(applicationContext,"Username is required",Toast.LENGTH_LONG).show()
                }
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(applicationContext,"Email is required",Toast.LENGTH_LONG).show()
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(applicationContext,"Password is required",Toast.LENGTH_LONG).show()
                }
                if (TextUtils.isEmpty(confirmPassword)){
                    Toast.makeText(applicationContext,"Confirm password is required",Toast.LENGTH_LONG).show()
                }

                if (password != confirmPassword){
                    Toast.makeText(applicationContext,"Password not match",Toast.LENGTH_LONG).show()
                }else{
                    registerUser(userName,email,password)
                }

            }
            btnSignIn.setOnClickListener {
                val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

    }

    private fun registerUser(userName:String,email:String,password:String){
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){task->
                if (task.isSuccessful){
                    val user : FirebaseUser? = auth.currentUser
                    val userId : String = user?.uid.toString()

                    databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)

                    val hashMap : HashMap<String,String> = HashMap()
                    hashMap.put("userId",userId)
                    hashMap.put("userName",userName)
                    hashMap.put("profileImage","")

                    databaseReference.setValue(hashMap).addOnCompleteListener(this){task->
                        if (task.isSuccessful){
                            //open home activity
                            binding.etEmail.setText("")
                            binding.etName.setText("")
                            binding.etPassword.setText("")
                            binding.etConfirmPassword.setText("")
                            val intent = Intent(this@SignUpActivity, UserActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }else{
                    Toast.makeText(applicationContext,"Sign Up error",Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                Toast.makeText(applicationContext,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
    }
}



