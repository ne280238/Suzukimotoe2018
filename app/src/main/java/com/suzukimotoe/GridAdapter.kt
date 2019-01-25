package com.suzukimotoe

import android.app.Application
import android.content.Context
import android.support.design.widget.CoordinatorLayout.Behavior.setTag
import android.view.View
import android.view.ViewGroup
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.Bitmap
import android.support.v4.content.ContextCompat.getSystemService
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


public class GridAdapter: BaseAdapter {

    private var imageList: List<Bitmap>
    private var pathList: List<String>
    private var keyList: List<String>
    private var context: Context? = null
    private var email: String? =null
    private var selectedid: Int? = null
    private var classid: Int? = null

    constructor(context: Context, iList: List<Bitmap>, pList: List<String>,kList: List<String>,email:String,id:Int,clid:Int) : super() {
        this.context = context
        imageList = iList
        pathList = pList
        keyList = kList
        this.email = email
        selectedid = id
        classid = clid
    }

    internal inner class ViewHolder {
        var imageView: ImageView? = null
        var button: Button? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.grid_items, parent, false)
        val viewHolder = ViewHolder()
        viewHolder.imageView = view.findViewById(R.id.image_view)
        viewHolder.button = view.findViewById(R.id.delete_button)
        view.setTag(viewHolder)
        viewHolder.imageView!!.setImageBitmap(imageList.get(position))
        //画像削除
        val storage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.reference
        viewHolder.button!!.setOnClickListener {
            val path = storageRef.child(pathList.get(position))
            path.delete().addOnSuccessListener {_ ->
                Log.d("削除成功","削除できた。")
                val db: FirebaseFirestore = FirebaseFirestore.getInstance()
                val docref: DocumentReference = db.collection(email.toString()).document(classid.toString() + "-" + selectedid.toString())
                val map: Map<String,Any> = mapOf(keyList.get(position) to FieldValue.delete())
                docref.update(map).addOnCompleteListener { _ ->
                    Log.d("DB削除成功","DBを削除できました。")
                    Toast.makeText(MyApplication.context, "画像を削除しました", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener{ exception ->  
                Log.d("削除失敗", "エラー:" + exception)
            }
        }

        return view
    }

    override fun getCount(): Int {
            // List<String> imgList の全要素数を返す
            return imageList.size
    }

    override fun getItem(position: Int): Any? {
            return null
    }

    override fun getItemId(position: Int): Long {
            return 0
    }

}

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MyApplication.context = applicationContext
    }

    companion object {
        var context: Context? = null
    }


}

