package com.suzukimotoe

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_lecinfo.*
import kotlinx.android.synthetic.main.activity_listlec.*
import java.util.*
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.app.Activity
import android.content.Intent




class ListLecActivity : AppCompatActivity() {

    private var helper: LecOpenHelper? = null
    private var db: SQLiteDatabase? = null

    private val dataList: ArrayList<Data> = arrayListOf()
    private val data = Data()

    val week = arrayOf("月曜日","火曜日","水曜日","木曜日","金曜日","土曜日")
    val time = arrayOf("1時限","2時限","3時限","4時限","5時限","6時限","7時限")

    var weekid = arrayOf("a","b","c","d","e","f")
    var timeid = arrayOf("1","2","3","4","5","6","7")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listlec)

        if (helper == null) {
            helper = LecOpenHelper(applicationContext)
        }

        if (db == null) {
            db = helper!!.readableDatabase
        }
        Log.d("debug", "**********Cursor")

        var cursor=forQuery()
        cursor.moveToFirst()

        // データを詰め込む
        for (i in 0 until cursor.count) {
            val data = Data()
            data.dataOne = cursor.getString(1)
            data.dataTwo = cursor.getString(6)
            dataList.add(data)
            cursor.moveToNext()
        }
        cursor.close()

        // リストにデータを受け渡す
        val arrayAdapter = ListAdapter(this, dataList)
        listView.adapter = arrayAdapter

        listView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                cursor=forQuery()
                cursor.moveToFirst()
                cursor.move(position)
                val msg =cursor.getString(1)+"がクリックされました"
                Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()

// intentの作成
                val intent2 = Intent()
                val weekNum = intent.getStringExtra("weekNum")
                val timeNum = intent.getStringExtra("timeNum")
                // intentへ添え字付で値を保持させる
                intent2.putExtra("value",cursor.getInt(0).toString())
                intent2.putExtra("weekid",weekid[weekNum.toInt()])
                intent2.putExtra("timeid",timeid[timeNum.toInt()])
                cursor.close()
                // 返却したい結果ステータスをセットする
                setResult(Activity.RESULT_OK, intent2)
                // アクティビティを終了させる
                finish()
            }
        })
    }

    fun forQuery(): Cursor {

        val weekNum = intent.getStringExtra("weekNum")
        val timeNum = intent.getStringExtra("timeNum")

        val cursor = db!!.query(
                "classinfo",
                arrayOf("id", "name", "term", "week", "time", "unit", "teacher", "evaluat", "URL"),
                "week='" + week[weekNum.toInt()] + "' AND time='" + time[timeNum.toInt()] + "'", null, null, null, null
        )

        return cursor
    }


}

