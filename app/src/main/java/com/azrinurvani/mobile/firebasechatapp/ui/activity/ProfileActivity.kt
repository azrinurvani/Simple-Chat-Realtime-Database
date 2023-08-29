package com.azrinurvani.mobile.firebasechatapp.ui.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.azrinurvani.mobile.firebasechatapp.R
import com.azrinurvani.mobile.firebasechatapp.databinding.ActivityProfileBinding
import com.azrinurvani.mobile.firebasechatapp.models.User
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.IOException
import java.util.BitSet
import java.util.UUID

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProfileBinding

    private lateinit var firebaseUser: FirebaseUser
    private  lateinit var databaseReference: DatabaseReference

    private var filePath : Uri? = null

    private final val PICK_IMAGE_REQUEST : Int = 2020

    private lateinit var storage : FirebaseStorage
    private lateinit var storageRef : StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        databaseReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                binding.etUserName.setText(user?.userName)

                if (user?.profileImage == ""){
                    binding.userImage.setImageResource(R.drawable.ic_launcher_foreground)
                }else{
                    Glide.with(binding.userImage).load(user?.profileImage).into(binding.userImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,error.message,Toast.LENGTH_LONG).show()
            }

        })

        binding.imgBack.setOnClickListener {
            onBackPressed()

        }
        binding.userImage.setOnClickListener {
            chooseImage()
        }
        binding.btnSave.setOnClickListener {
            uploadImage()
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    private fun chooseImage(){
        val intent = Intent()
        intent.type = "image/"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"Select Image"),PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode !=null){
            filePath = data?.data

            try {
                var bitmap : Bitmap = MediaStore.Images.Media.getBitmap(contentResolver,filePath)
                binding.userImage.setImageBitmap(bitmap)
                binding.btnSave.visibility = View.VISIBLE
            }catch (e : IOException){
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage(){
        if (filePath!=null){

            val ref : StorageReference =  storageRef.child("image/"+UUID.randomUUID().toString())
            ref.putFile(filePath!!)
                .addOnSuccessListener {


                    val hashMap : HashMap<String,String> = HashMap()

                    hashMap.put("userName",binding.etUserName.text.toString())
                    hashMap.put("profileImage",filePath.toString())

                    databaseReference.updateChildren(hashMap as Map<String,Any>)

                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext,"Upload Success", Toast.LENGTH_LONG).show()
                    binding.btnSave.visibility = View.GONE

                }.addOnFailureListener{

                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext,"Failed : "+it.message,Toast.LENGTH_LONG).show()

                }

        }
    }
}