package com.suzukimotoe

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_reglec.*
import java.util.ArrayList

class RegLecActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var helper: LecOpenHelper? = null
    private var db: SQLiteDatabase? = null
    var lecId:String?=null
    var lecName:String?=null

    var weekid = arrayOf("a","b","c","d","e","f")
    var timeid = arrayOf("1","2","3","4","5","6","7")

    private val timetableList: ArrayList<Timetable> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reglec)

        //サブメニュー
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val drawer = findViewById<View>(R.id.drawer_reglec_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<View>(R.id.nav_view_reglec) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
    }

    fun readData() {
        if (helper == null) {
            helper = LecOpenHelper(applicationContext)
        }

        if (db == null) {
            db = helper!!.readableDatabase
        }
        Log.d("debug", "**********Cursor")
        val cursor = db!!.query(
                "classinfo",
                arrayOf("id", "name","term","week","time","unit","teacher","evaluat","URL"), "id="+lecId, null, null, null, null
        )
        cursor.moveToFirst()
        lecName = cursor.getString(1)
        cursor.close()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        // startActivityForResult()の際に指定した識別コードとの比較
        if( requestCode == 1001 ){
            // 返却結果ステータスとの比較
            if( resultCode == Activity.RESULT_OK ){
                if (intent!=null) {
                    // 返却されてきたintentから値を取り出す

                    val timetable=Timetable()
                    timetable.weekid = intent.getStringExtra("weekid")
                    timetable.timeid=intent.getStringExtra("timeid")
                    timetable.value=intent.getStringExtra("value")
                    timetableList.add(timetable)

                    val box = timetable.weekid + timetable.timeid
                    val res = resources
                    val boxid = res.getIdentifier(box, "id", getPackageName());
                    val boxbutton = findViewById<Button>(boxid)
                    if (timetable.value != null) {
                        lecId=timetable.value.toString()
                        readData()
                        boxbutton.text = lecName
                    }

                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val uid = FirebaseAuth.getInstance().uid
        val database = FirebaseDatabase.getInstance()

        val key = "lesson"
        val ref = database.getReference(key)



        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for ((i, v) in weekid.withIndex()) {
                    for ((j, w) in timeid.withIndex()) {
                        val box = v + w.toString()
                        val res = resources
                        val boxid = res.getIdentifier(box, "id", getPackageName());
                        val boxbutton = findViewById<Button>(boxid)
                        val value = dataSnapshot.child(uid.toString()).child(v).child(w.toString()).value
                        if (value != null) {
                            lecId=value.toString()
                            readData()
                            boxbutton.text = lecName
                        }
                        boxbutton.setOnClickListener {
                            val reqestcode = 1001
                            val intent = Intent(this@RegLecActivity, ListLecActivity::class.java)
                            intent.putExtra("weekNum", i.toString())
                            intent.putExtra("timeNum", j.toString())
                            startActivityForResult(intent, reqestcode)
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("onCancelled", "error:", error.toException())
            }
        })

        register.setOnClickListener{
            val msg ="講義を登録します。少しお待ちください"
            Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()

            var uid = FirebaseAuth.getInstance().uid
            val database = FirebaseDatabase.getInstance()
            var key = "lesson"
            var ref = database.getReference(key)
            timetableList.forEach {
                ref.child(uid.toString()).child(it.weekid!!).child(it.timeid!!).setValue(it.value)
            }
            val intent = Intent(this@RegLecActivity, TimetableActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        if (drawer_reglec_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_reglec_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.submenu, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_zikanwari -> {
                val intent = Intent(application, TimetableActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_kougitouroku -> {

            }
            R.id.nav_userinfo -> {
                val intent = Intent(application, UserinfoActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(application, LoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }

        val drawer = findViewById<View>(R.id.drawer_reglec_layout) as? DrawerLayout
        if(drawer != null) {
            drawer.closeDrawer(GravityCompat.START)
        }
        return true
    }

}
