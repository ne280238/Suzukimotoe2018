package com.suzukimotoe

import android.content.Context
import android.content.res.Resources
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.util.*

// データ格納用クラス
class Data {
    var dataOne: String? = null
    var dataTwo: String? = null
}

class Timetable{
    var weekid :String? = null
    var timeid :String? = null
    var value :String? = null
}

class ListAdapter(context: Context, objects: List<Data>) : ArrayAdapter<Data>(context, 0, objects) {
    private val inflater: LayoutInflater
    // values/colors.xmlより設定値を取得するために利用。
    private val r: Resources

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        r = context.resources
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var view = view
        // layout/raw.xmlを紐付ける
        if (view == null) {
            view = inflater.inflate(R.layout.raw, parent, false)
        }
        val data = this.getItem(position)
        val tvData1 = view!!.findViewById(R.id.raw1) as TextView
        val tvData2 = view.findViewById(R.id.raw2) as TextView
        if (data != null) {
            tvData1.text = data.dataOne
            tvData2.text = data.dataTwo
        }
        return view
    }
}
