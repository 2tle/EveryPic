package io.twotle.everypic.upload

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.twotle.everypic.MainActivity
import io.twotle.everypic.R
import io.twotle.everypic.auth.FirebaseObj
import io.twotle.everypic.databinding.ActivityEditPicBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditPicActivity : AppCompatActivity() {
    lateinit var binding : ActivityEditPicBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_edit_pic)
        binding.epaDesc.setText( intent.getStringExtra("desc").toString())
        //Glide.with(holder.itemView).load(Firebase.storage.reference.child(data[position].pictureUUID)).into(holder.binding.ipImg)
        //Glide.with(this).load(Firebase.storage.reference.child(intent.getStringExtra("photoUid").toString())).into(binding.epaImg)
        val stor = Firebase.storage.reference

        stor.child(intent.getStringExtra("photoUid").toString()).downloadUrl.addOnSuccessListener {
            Glide.with(this).load(it).into(binding.epaImg)
        }
        binding.epaUpload.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val result = FirebaseObj.editPic(intent.getStringExtra("uid").toString(), binding.epaDesc.text.toString())
                if(result) {
                    val builder = AlertDialog.Builder(this@EditPicActivity)
                    builder.setTitle("수정 성공")
                    builder.setPositiveButton("확인") { _:DialogInterface?, _:Int ->
                        startActivity(Intent(this@EditPicActivity, MainActivity::class.java))
                    }
                    builder.show()
                } else {
                    val builder = AlertDialog.Builder(this@EditPicActivity)
                    builder.setTitle("수정 실패")
                    builder.setMessage(FirebaseObj.errorDesc)
                    builder.setNegativeButton("확인") { _:DialogInterface?, _:Int ->
                        startActivity(Intent(this@EditPicActivity, MainActivity::class.java))
                    }
                    builder.show()
                }
            }
        }
    }
}