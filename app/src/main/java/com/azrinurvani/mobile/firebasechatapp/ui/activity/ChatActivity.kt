package com.azrinurvani.mobile.firebasechatapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.azrinurvani.mobile.firebasechatapp.R
import com.azrinurvani.mobile.firebasechatapp.databinding.ActivityChatBinding
import com.azrinurvani.mobile.firebasechatapp.network.RetrofitInstance
import com.azrinurvani.mobile.firebasechatapp.models.Chat
import com.azrinurvani.mobile.firebasechatapp.models.NotificationData
import com.azrinurvani.mobile.firebasechatapp.models.PushNotification
import com.azrinurvani.mobile.firebasechatapp.models.User
import com.azrinurvani.mobile.firebasechatapp.ui.adapter.ChatAdapter
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception


class ChatActivity : AppCompatActivity() {

    private lateinit var binding : ActivityChatBinding

    var firebaseUser : FirebaseUser?= null
    var reference : DatabaseReference?= null

    var chatList = ArrayList<Chat>()
    var topic = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        binding.rvChat.layoutManager = LinearLayoutManager(this@ChatActivity, LinearLayoutManager.VERTICAL,false)

        var userId = intent.getStringExtra("userId")
        var userName = intent.getStringExtra("userName")

        firebaseUser = FirebaseAuth.getInstance().currentUser

        reference =  FirebaseDatabase.getInstance().getReference("Users").child(userId!!)

        reference?.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                val user = snapshot.getValue(User::class.java)
                binding.tvUserName.text = user?.userName

                val currentUser = snapshot.getValue(User::class.java)
                if (currentUser!!.profileImage == ""){
                    binding.imgProfile.setImageResource(R.drawable.ic_launcher_background)
                }else{
                    Glide.with(this@ChatActivity).load(currentUser.profileImage).into(binding.imgProfile)
                }

            }

            override fun onCancelled(error: DatabaseError) {
               Toast.makeText(applicationContext,error.message,Toast.LENGTH_LONG).show()
            }

        })
        binding.btnSendMessage.setOnClickListener {
            val message : String = binding.etChat.text.toString()

            if (message.isEmpty()){
                Toast.makeText(applicationContext,"message is empty",Toast.LENGTH_LONG).show()
            }else{
                sendMessage(firebaseUser?.uid.toString(),userId,message)
                binding.etChat.setText("")

                if (userId!=null){
                    topic = "/topics/$userId"
                    PushNotification(NotificationData(userName.toString(),message), topic).also{
                        sendNotification(it)
                    }
                }


            }
        }

        readMessage(firebaseUser!!.uid,userId)

    }

    private fun sendMessage(senderId:String,receiverId:String,message:String){
        var reference =  FirebaseDatabase.getInstance().reference

        var hashMap:HashMap<String,String> = HashMap()
        hashMap.put("senderId",senderId)
        hashMap.put("receiverId",receiverId)
        hashMap.put("message",message)

        reference.child("Chat").push().setValue(hashMap)
    }

    private fun readMessage(senderId : String,receiverId : String){
        var databaseReference = FirebaseDatabase.getInstance().getReference("Chat")

        databaseReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children){
                    val chat : Chat? = dataSnapshot.getValue(Chat::class.java)

                    if (chat?.senderId == senderId && chat.receiverId== receiverId
                        || chat?.senderId == receiverId && chat.receiverId == receiverId){
                        chatList.add(chat)
                    }


                }
                val chatAdapter = ChatAdapter(this@ChatActivity,chatList)
                binding.rvChat.adapter = chatAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,error.message,Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if (response.isSuccessful){
//                Toast.makeText(applicationContext,"Response success",Toast.LENGTH_LONG).show()
                Log.d(TAG, "sendNotification: success")

            }else{
//                Toast.makeText(applicationContext,response.errorBody().toString(),Toast.LENGTH_LONG).show()
                Log.e(TAG, "sendNotification: ${response.errorBody()}")
            }
        }catch (e:Exception){
            Log.e(TAG, "sendNotification: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "ChatActivity"
    }
}