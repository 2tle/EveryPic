package io.twotle.everypic.upload

import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import com.google.firebase.ktx.Firebase
import io.twotle.everypic.MainActivity
import io.twotle.everypic.R
import io.twotle.everypic.auth.FirebaseObj
import io.twotle.everypic.databinding.ActivityPostPicBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PostPicActivity : AppCompatActivity() {

    lateinit var binding: ActivityPostPicBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_pic)
        binding.ppaUpload.isClickable = false
        val galleryResultCallback = registerForActivityResult(ActivityResultContracts.GetContent()) {
            try {
                val iStream = contentResolver.openInputStream(it)
                val img = BitmapFactory.decodeStream(iStream)
                iStream?.close()
                binding.ppaImg.setImageBitmap(img)
                binding.ppaUpload.isClickable = true
            } catch(e: Exception) {

            }
        }

        binding.ppaImg.setOnClickListener {
            galleryResultCallback.launch("image/*")
        }

        binding.ppaUpload.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val result = FirebaseObj.postPic(binding.ppaImg.drawable.toBitmap(),binding.ppaDesc.text.toString())
                if(result) {
                    val builder = AlertDialog.Builder(this@PostPicActivity)
                    builder.setTitle("업로드 성공")
                    builder.setPositiveButton("확인") { _: DialogInterface?, _:Int ->
                        startActivity(Intent(this@PostPicActivity, MainActivity::class.java))
                    }
                    builder.show()
                } else {
                    val builder = AlertDialog.Builder(this@PostPicActivity)
                    builder.setTitle("업로드 실패")
                    builder.setMessage(FirebaseObj.errorDesc)
                    builder.setNegativeButton("확인") { _: DialogInterface?, _:Int ->
                        startActivity(Intent(this@PostPicActivity, MainActivity::class.java))
                    }
                    builder.show()
                }
            }
        }


    }
}