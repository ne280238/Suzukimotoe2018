package com.suzukimotoe

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.nav_header_submenu.*
import java.util.*
import android.support.annotation.NonNull
import android.util.Log
import android.widget.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.*
import com.google.firebase.storage.FileDownloadTask
import java.io.File
import java.lang.Exception
import java.lang.Thread


open class ListActivity : AppCompatActivity() {

    private var filepath: Uri? = null
    private var selectedId: Int = 0
    private var classId: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val intent: Intent = getIntent()
        val selectedText = intent.getStringExtra("Text")
        val intent_classname = intent.getStringExtra("class")
        selectedId = intent.getIntExtra("class_id",0)
        classId = intent.getIntExtra("classid",0)

        //授業名
        val classname_name: TextView = findViewById(R.id.classname_name)
        classname_name.setText(intent_classname)

        //授業回
        val textview: TextView = findViewById(R.id.classname_intent)
        textview.setText(selectedText)

        //画像ダウンロード
        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.getCurrentUser()
        var email:String? = null
        if(user != null){
            email = user.email
        }
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val docRef: DocumentReference =db.collection(email.toString()).document(classId.toString() + "-" + selectedId.toString())
        docRef.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document: DocumentSnapshot = task.getResult()!!
                        if(document.exists()) {
                            Log.d("データベースDL成功", "DocumentSnapshot data: " + document.data)
                            val list: List<String> = document.data!!.flatMap { listOf(it.value.toString()) }
                            val keylist: List<String> = document.data!!.flatMap { listOf(it.key.toString()) }
                            val imagelist: MutableList<Bitmap> = mutableListOf()
                            val storage = FirebaseStorage.getInstance()
                            val storageRef: StorageReference = storage.reference

                            //ローカルへDL
                            for(x in list){
                                val path = storageRef.child(x)
                                val ONE_MEGABYTE: Long = 1024 * 1024 * 200
                                path.getBytes(ONE_MEGABYTE)
                                        .addOnSuccessListener { bytes ->
                                            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                            imagelist.add(bmp)
                                            //GridView生成
                                            if (imagelist.size == list.size) {
                                                val adapter: GridAdapter = GridAdapter(this.applicationContext, imagelist, list, keylist, email.toString(), selectedId, classId)
                                                gridview.setAdapter(adapter)
                                            }
                                        }
                                        .addOnFailureListener {
                                            Log.d("画像DL失敗","sippai")
                                        }
                            }

                        }else{
                            Log.d("ドキュメントがない","No such document")
                        }
                    } else {
                        Log.d("データベースDL失敗", "Error getting documents: " + task.exception)
                    }
                }

        //メモ
        val memowrite: EditText = findViewById(R.id.memo)
        val memoup: Button = findViewById(R.id.upload)
        val memoread: TextView = findViewById(R.id.memo_dl)
        val memodocRef: DocumentReference =db.collection(email.toString()).document("memo" + classId.toString() + "-" +selectedId.toString())
        //メモDL
        memodocRef.get().addOnCompleteListener { task ->
            if(task.isSuccessful){
                val document: DocumentSnapshot = task.getResult()!!
                if(document.exists()){
                    Log.d("メモデータベースDL成功", "DocumentSnapshot data: " + document.data)
                    memoread.setText(document.data!!["memo"].toString())
                }
            }
        }
        //メモUP
        memoup.setOnClickListener {
            val memomap: Map<String,Any> = mapOf("memo" to memowrite.text.toString())
            memodocRef.set(memomap, SetOptions.merge()).addOnSuccessListener { _ ->
                Log.d("memo_upload","アップロード成功")
                Toast.makeText(applicationContext, "メモをアップロードしました", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                Log.d("memo_upload", e.toString())
            }
        }


        //画像アップロード
        val up_im: Button = findViewById(R.id.upload_image)
        up_im.setOnClickListener {
            val image = Intent()
            image.type = "image/*"
            image.action = Intent.ACTION_GET_CONTENT
            if(selectedId != 0) {
                startActivityForResult(Intent.createChooser(image, "画像を選んでください"), 1234)
            }else{
                println("アップロード失敗")
            }
        }

        //更新
        val reload: Button = findViewById(R.id.reload)
        reload.setOnClickListener {
            val reintent:Intent = getIntent()
            overridePendingTransition(0,0)
            reintent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            finish()

            overridePendingTransition(0,0)
            startActivity(reintent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1234 &&
                resultCode == Activity.RESULT_OK &&
                data != null && data.data != null) {
                    filepath = data.data
                    val mAuth = FirebaseAuth.getInstance()
                    val user = mAuth.getCurrentUser()
                    var email:String? = null
                    if(user != null){
                         email = user.email
                    }
                    val storage = FirebaseStorage.getInstance()
                    val storageRef: StorageReference = storage.reference
                    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
                    val datastore = getSharedPreferences("Datastore", Context.MODE_PRIVATE)
                    var imagecount = datastore.getInt("DataInt",0).toInt()
                    val imageRef: StorageReference = storageRef.child("images").child(email.toString()).child(classId.toString()).child(selectedId.toString()).child("image"+imagecount.toString())
                    val imagepath: String = "images/"+ email.toString() + "/" + classId.toString() + "/" + selectedId.toString() + "/image" + imagecount.toString()
                    val path: String = "path" + imagecount.toString()
                    val map: Map<String,Any> = mapOf(path to imagepath)
                    imageRef.putFile(filepath!!)
                                .addOnSuccessListener {
                                    Toast.makeText(applicationContext, "画像をアップロードしました", Toast.LENGTH_SHORT).show()
                                    imagecount = imagecount + 1
                                    val editor = datastore.edit()
                                    editor.putInt("DataInt",imagecount)
                                    editor.apply()
                                    db.collection(email.toString()).document(classId.toString() + "-" + selectedId.toString())
                                            .set(map, SetOptions.merge())
                                            .addOnSuccessListener { _ -> Log.d("データベース投入成功", "DocumentSnapshot successfully written!") }
                                            .addOnFailureListener { e -> Log.w("データベース投入失敗", "Error writing document", e) }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(applicationContext, "画像アップロードに失敗しました", Toast.LENGTH_SHORT).show()
                                }
                                .addOnProgressListener { _ ->

                                }
        }
    }

}

