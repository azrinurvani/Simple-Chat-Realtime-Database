package com.azrinurvani.mobile.firebasechatapp.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.azrinurvani.mobile.firebasechatapp.R
import com.azrinurvani.mobile.firebasechatapp.databinding.ItemLeftBinding
import com.azrinurvani.mobile.firebasechatapp.databinding.ItemRightBinding
import com.azrinurvani.mobile.firebasechatapp.databinding.ItemUserBinding
import com.azrinurvani.mobile.firebasechatapp.models.Chat
import com.azrinurvani.mobile.firebasechatapp.models.User
import com.azrinurvani.mobile.firebasechatapp.ui.activity.ChatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ChatAdapter(private val context:Context, private val chatList : ArrayList<Chat>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {


    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1

    private var firebaseUser : FirebaseUser? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {

        if (viewType== MESSAGE_TYPE_RIGHT){
           val binding = ItemLeftBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return ChatViewHolder(binding.root)
        }else{
            val binding = ItemRightBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return ChatViewHolder(binding.root)
        }

    }

    override fun getItemCount(): Int = chatList.size

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        holder.tvMessage.text = chat.message

    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (chatList[position].senderId == firebaseUser!!.uid){
            return MESSAGE_TYPE_RIGHT
        }else{
            return MESSAGE_TYPE_LEFT
        }
    }

    class ChatViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val tvMessage : TextView =  view.findViewById(R.id.tv_message)
//        val imgProfile : ImageView =  view.findViewById(R.id.imgProfile)
    }
}