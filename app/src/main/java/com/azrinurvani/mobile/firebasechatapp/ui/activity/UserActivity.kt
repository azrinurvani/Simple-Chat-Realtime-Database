package com.azrinurvani.mobile.firebasechatapp.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.azrinurvani.mobile.firebasechatapp.R
import com.azrinurvani.mobile.firebasechatapp.databinding.ActivityUserBinding
import com.azrinurvani.mobile.firebasechatapp.firebase.FirebaseService
import com.azrinurvani.mobile.firebasechatapp.models.User
import com.azrinurvani.mobile.firebasechatapp.ui.adapter.UserAdapter
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    var userList = ArrayList<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseService.sharedPreferences = getSharedPreferences("sharedPref",Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            FirebaseService.tokenCache = it
        }


        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        getUserList()
        binding.rvUser.layoutManager = LinearLayoutManager(this@UserActivity,LinearLayoutManager.VERTICAL,false)
        binding.imgProfile.setOnClickListener {
            val intent = Intent(this@UserActivity,ProfileActivity::class.java)
            startActivity(intent)

        }

    }

    private fun getUserList(){
        var firebase : FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        var databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        var userId = firebase.uid
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userId")

        databaseReference.addValueEventListener(object : ValueEventListener{
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()

                val currentUser = snapshot.getValue(User::class.java)
                if (currentUser!!.profileImage == ""){
                    binding.imgProfile.setImageDrawable(getDrawable(R.drawable.ic_launcher_background))
                }else{
                    Glide.with(this@UserActivity).load(currentUser.profileImage).into(binding.imgProfile)
                }

                for (dataSnapshot: DataSnapshot in snapshot.children){
                    val user : User? = dataSnapshot.getValue(User::class.java)

//                    if (user?.userId == firebase.uid){
//                        userList.add(user)
//                    }
                    if (user != null) {
                        userList.add(user)
                    }
                    Log.d(TAG, "onDataChange: ${userList[0].userId}")
                }
                val userAdapter = UserAdapter(this@UserActivity,userList)
                binding.rvUser.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,error.message,Toast.LENGTH_LONG).show()
            }

        })

    }

    companion object {
        private const val TAG = "UserActivity"
    }
}