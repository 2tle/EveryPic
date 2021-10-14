package io.twotle.everypic

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.twotle.everypic.auth.FirebaseObj
import io.twotle.everypic.databinding.ActivityPicViewBinding
import io.twotle.everypic.upload.EditPicActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PicViewActivity : AppCompatActivity() {
    lateinit var binding : ActivityPicViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_pic_view)

        binding.pvaDesc.setText( intent.getStringExtra("desc").toString())
        //Glide.with(holder.itemView).load(Firebase.storage.reference.child(data[position].pictureUUID)).into(holder.binding.ipImg)
        //Glide.with(this).load(Firebase.storage.reference.child(intent.getStringExtra("photoUid").toString())).into(binding.pvaImg)
        val stor = Firebase.storage.reference

        stor.child(intent.getStringExtra("photoUid").toString()).downloadUrl.addOnSuccessListener {
            Glide.with(this).load(it).into(binding.pvaImg)
        }

        binding.pvaRemove.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val result = FirebaseObj.removePic(intent.getStringExtra("uid").toString(), intent.getStringExtra("photoUid").toString())
                if(result) {
                    val builder = AlertDialog.Builder(this@PicViewActivity)
                    builder.setTitle("삭제 성공")
                    builder.setPositiveButton("확인") { _: DialogInterface?, _:Int ->
                        startActivity(Intent(this@PicViewActivity, MainActivity::class.java))
                    }
                    builder.show()
                } else {
                    val builder = AlertDialog.Builder(this@PicViewActivity)
                    builder.setTitle("삭제 실패")
                    builder.setMessage(FirebaseObj.errorDesc)
                    builder.setNegativeButton("확인") { _: DialogInterface?, _:Int ->
                        startActivity(Intent(this@PicViewActivity, MainActivity::class.java))
                    }
                    builder.show()
                }
            }
        }

        binding.pvaEdit.setOnClickListener {
            val intent1 = Intent(this, EditPicActivity::class.java)
            intent1.putExtra("desc",intent.getStringExtra("desc").toString())
            intent1.putExtra("uid",intent.getStringExtra("uid").toString())
            intent1.putExtra("photoUid",intent.getStringExtra("photoUid").toString())
            startActivity(intent1)
        }
    }
}