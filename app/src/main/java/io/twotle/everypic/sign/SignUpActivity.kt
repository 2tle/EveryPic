package io.twotle.everypic.sign

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.twotle.everypic.MainActivity
import io.twotle.everypic.R
import io.twotle.everypic.auth.FirebaseObj
import io.twotle.everypic.databinding.ActivitySignUpBinding
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    private lateinit var sf: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)



        binding.suaSignup.setOnClickListener {

            if(!emailCheck(binding.suaEmail.text.toString()) or !passwordCheck(binding.suaPw.text.toString())) {
                showAlertDialog("규칙 확인","이메일 형식 및 비밀번호는 특수문자 영어 숫자를 조합해 8자 이상 16자 이하로 해주세요")
            } else {
                sf = getSharedPreferences("Auth", MODE_PRIVATE)
                editor = sf.edit()
                editor.putString("email", binding.suaEmail.text.toString())
                editor.commit()
                editor.putString("pw", binding.suaPw.text.toString())
                editor.commit()

                signUp(
                    binding.suaEmail.text.toString(),
                    binding.suaPw.text.toString()
                )
            }

        }







    }

    fun emailCheck(email: String): Boolean {
        val emailRegex =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        return Pattern.matches(emailRegex, email)

    }

    fun passwordCheck(pw: String): Boolean {
        val pwRegex =
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#\$%^&*()+|=])[A-Za-z\\d~!@#\$%^&*()+|=]{8,16}"
        return Pattern.matches(pwRegex, pw)
    }

    fun signUp(email: String, pw: String) {
        Firebase.auth.createUserWithEmailAndPassword(email,pw).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                FirebaseObj.uid = Firebase.auth.currentUser?.uid.toString()
                FirebaseObj.email = Firebase.auth.currentUser?.email.toString()
                goMainActivity()
            } else {
                showAlertDialog("회원가입 오류",task.exception?.message.toString())
            }
        }
    }

    fun showAlertDialog(title: String, message: String) {
        var builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK", null)
        builder.show()

    }

    fun goMainActivity() {
        var builder = AlertDialog.Builder(this)
        builder.setTitle("회원가입 성공")
        builder.setMessage("회원이 된 것을 축하드립니다.")
        builder.setPositiveButton("확인") { _: DialogInterface, _: Int ->
            startActivity(Intent(this, MainActivity::class.java))
        }
        builder.show()
    }

}