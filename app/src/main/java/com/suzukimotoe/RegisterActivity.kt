package com.suzukimotoe

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_register.*
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth


class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        table.setOnClickListener {
            // 問題があれば弾く
            if (mail.text.toString() == "" || pass.text.toString() == "" ||repass.text.toString() == "" || mail.text.toString() == "") {
                Toast.makeText(applicationContext,"入力されていない項目があります",Toast.LENGTH_SHORT).show()
            }else if (!pass.text.toString().equals(repass.text.toString())) {
                Toast.makeText(applicationContext,"再入力されたパスワードが違います",Toast.LENGTH_SHORT).show()
            } else if (pass.text.toString().length < 8) {
                Toast.makeText(applicationContext,"パスワードは8文字以上です",Toast.LENGTH_SHORT).show()
            } else {
                //ログイン処理

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                val login = mAuth.createUserWithEmailAndPassword(mail.text.toString(), pass.text.toString())

                login.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //画面遷移
                        finish()
                    } else {
                        Toast.makeText(applicationContext,"アカウント作成失敗",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
