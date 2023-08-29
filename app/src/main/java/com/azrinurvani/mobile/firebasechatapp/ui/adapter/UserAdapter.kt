package com.azrinurvani.mobile.firebasechatapp.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azrinurvani.mobile.firebasechatapp.R
import com.azrinurvani.mobile.firebasechatapp.databinding.ItemUserBinding
import com.azrinurvani.mobile.firebasechatapp.models.User
import com.azrinurvani.mobile.firebasechatapp.ui.activity.ChatActivity
import com.bumptech.glide.Glide

class UserAdapter(private val context:Context,private val userList : ArrayList<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private lateinit var binding: ItemUserBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.binding.tvUserName.text= user.userName
        Glide.with(context)
            .load(user.profileImage)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.binding.userImage)

        holder.binding.layoutUser.setOnClickListener {
            val intent = Intent(context,ChatActivity::class.java)
            intent.putExtra("userId",user.userId)
            intent.putExtra("userName",user.userName)
            context.startActivity(intent)

        }
    }


    class UserViewHolder(val binding : ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}