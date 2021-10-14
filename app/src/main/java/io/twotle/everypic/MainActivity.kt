package io.twotle.everypic

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import io.twotle.everypic.auth.FirebaseObj
import io.twotle.everypic.data.PicDTO
import io.twotle.everypic.databinding.ActivityMainBinding
import io.twotle.everypic.upload.PostPicActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    var data : ArrayList<PicDTO>? = null
    private var backKeyPressedTime: Long = 0
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.maUpload.setOnClickListener {
            startActivity(Intent(this,PostPicActivity::class.java))
        }

        CoroutineScope(Dispatchers.Main).launch  {
            data = FirebaseObj.getPicList()
            if(data != null) {
                binding.maRecycler.adapter = MainRecyclerAdapter(data!!)
            } else {
                Log.d(">>",FirebaseObj.errorDesc+"123")
            }
            binding.maRecycler.adapter?.notifyDataSetChanged()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.Main).launch  {
            data = FirebaseObj.getPicList()
            if(data != null) {
                binding.maRecycler.adapter = MainRecyclerAdapter(data!!)
            } else {
                Log.d(">>",FirebaseObj.errorDesc+"123")
            }

            binding.maRecycler.adapter?.notifyDataSetChanged()
        }

    }

    override fun onBackPressed() {
        lateinit var toast: Toast
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis()
            toast = Toast.makeText(this, "뒤로가기를 한번 더 누르면 종료됩니다.", Toast.LENGTH_LONG)
            toast.show()
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finishAffinity()
            finish()
            System.runFinalization()
            moveTaskToBack(true)
            finishAndRemoveTask()
            exitProcess(0)
        }
    }
}