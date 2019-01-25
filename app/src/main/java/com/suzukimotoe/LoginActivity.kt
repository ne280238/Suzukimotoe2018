package com.suzukimotoe

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.widget.Toast
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import android.R.attr.password
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.title.*


class LoginActivity : AppCompatActivity(), View.OnTouchListener {

    private val REQUEST_CODE: Int = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.title)
        titleimage.setOnTouchListener(this)

        //許可しているかの場合分け
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

        }else{
            rqpermisiion()
        }

    }

    private fun rqpermisiion(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            //許可を求める
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE)
        }else{
            Toast.makeText(applicationContext,"許可してください",Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            REQUEST_CODE ->{
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(applicationContext,"アプリが落ちます",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        when(event!!.action){
            MotionEvent.ACTION_DOWN ->{
                //ログイン画面へ
                setScreenLogin()
            }
        }
        return true
    }

    private fun setScreenLogin(){
        setContentView(R.layout.activity_login)
        val mAuth = FirebaseAuth.getInstance()

        table.setOnClickListener {

            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        login.setOnClickListener {

            val login = mAuth.signInWithEmailAndPassword(mail.text.toString(), pass.text.toString())
            login.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@LoginActivity, TimetableActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext,"ログインできませんでした",Toast.LENGTH_SHORT).show()
                    Log.w("error",task.exception)
                }
            }
        }
    }
}
