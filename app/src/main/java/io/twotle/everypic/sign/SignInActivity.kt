package io.twotle.everypic.sign

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.twotle.everypic.MainActivity
import io.twotle.everypic.R
import io.twotle.everypic.auth.FirebaseObj
import io.twotle.everypic.databinding.ActivitySignInBinding
import io.twotle.everypic.databinding.ActivitySignInBindingImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import kotlin.system.exitProcess

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var sf: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var backKeyPressedTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)

        binding.siaSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        sf = getSharedPreferences("Auth", MODE_PRIVATE)
        editor = sf.edit()
        Log.d(">!<", sf.getString("pw", "").toString())
        val str = sf.getString("pw", "").toString()
        if ((!sf.getString("email", "").equals("")) and !(sf.getString("pw", "").equals(""))) {
            Log.d(">?<", sf.getString("pw", "").toString())
            login(sf.getString("email", "").toString(), str, 0, this)
        }

        binding.siaSignin.setOnClickListener {
            if(EmailCheck(binding.siaEmail.text.toString()) and PasswordCheck(binding.siaPw.text.toString())) {
                editor.putString("email", binding.siaEmail.text.toString())
                editor.commit()
                editor.putString("pw", binding.siaPw.text.toString())
                editor.commit()
                login(binding.siaEmail.text.toString(), binding.siaPw.text.toString(), 1, this)
            } else {
                showAlertNoListener(
                    "로그인 오류",
                    "이메일 및 비밀번호 형식을 확인해주세요. 비밀번호는 특수문자, 숫자, 영어를 하나 이상 포함한 8자 이상 16자 이하입니다."
                )
            }
        }
    }

    fun showAlertNoListener(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK", null)
        builder.show()
    }

    fun EmailCheck(email: String): Boolean {
        val emailRegex =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        return Pattern.matches(emailRegex, email)

    }

    fun PasswordCheck(pw: String): Boolean {
        val pwRegex =
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#\$%^&*()+|=])[A-Za-z\\d~!@#\$%^&*()+|=]{8,16}"
        return Pattern.matches(pwRegex, pw)
    }

    fun login(email: String, pw: String, mode: Int, context: Context) {
        Firebase.auth.signInWithEmailAndPassword(email, pw).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                CoroutineScope(Dispatchers.Main).launch {
                    FirebaseObj.uid = Firebase.auth.currentUser?.uid.toString()
                    FirebaseObj.email = Firebase.auth.currentUser?.email.toString()
                    startActivity(Intent(context, MainActivity::class.java))
                }
            } else {
                if (mode == 0) showAlertNoListener("자동로그인 오류", task.exception?.message.toString())
                else showAlertNoListener("로그인 오류", task.exception?.message.toString())
            }

        }

    }


    override fun onBackPressed() {
        lateinit var toast: Toast
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis()
            toast = Toast.makeText(this, "뒤로가기를 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT)
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