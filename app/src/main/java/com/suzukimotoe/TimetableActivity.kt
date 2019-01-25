package com.suzukimotoe

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import com.google.firebase.internal.FirebaseAppHelper.getToken
import com.google.firebase.auth.GetTokenResult
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
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
import android.widget.TextView
import com.suzukimotoe.R.layout.activity_register
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.internal.FirebaseAppHelper.getUid
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_lecinfo.*
import kotlinx.android.synthetic.main.activity_timetable.*


class TimetableActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener  {

    private var helper: LecOpenHelper? = null
    private var db: SQLiteDatabase? = null
    var lecId:String?=null
    var lecName:String?=null
    val boxlecid=mutableMapOf(0 to "a")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timetable)
        var uid = FirebaseAuth.getInstance().uid

        val database = FirebaseDatabase.getInstance()

        var key = "lesson"
        var ref = database.getReference(key)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var week = arrayOf("a","b","c","d","e","f")
                for ((i, v) in week.withIndex()){
                    var x=1
                    while (x<8) {
                        var box = v+x.toString()
                        val res = resources
                        var boxid = res.getIdentifier(box,"id", getPackageName());
                        var boxbutton = findViewById<Button>(boxid)
                        var value = dataSnapshot.child(uid.toString()).child(v).child(x.toString()).value
                        if(value!=null){
                            lecId=value.toString()
                            boxlecid += boxid to lecId.toString()
                            readData()
                            boxbutton.text=lecName
                            boxbutton.setOnClickListener {
                                println("デバッグ"+it.id)
                                lecId=boxlecid[it.id]
                                val intent = Intent(this@TimetableActivity, LecInfoActivity::class.java)
                               intent.putExtra("lecId",lecId)
                                startActivity(intent)
                            }
                        }
                        x++
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("onCancelled", "error:", error.toException())
            }
        })

        //サブメニュー
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val drawer = findViewById<View>(R.id.drawer_timetable_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<View>(R.id.nav_view_timetable) as NavigationView
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


    override fun onBackPressed() {
        if (drawer_timetable_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_timetable_layout.closeDrawer(GravityCompat.START)
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
                val intent = Intent(application, RegLecActivity::class.java)
                startActivity(intent)
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

        val drawer = findViewById<View>(R.id.drawer_bbs_layout) as? DrawerLayout
        if(drawer != null) {
            drawer.closeDrawer(GravityCompat.START)
        }
        return true
    }
}

