package com.suzukimotoe

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_lecinfo.*
import java.sql.SQLException
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_lecinfo.*
import java.net.URI


class LecInfoActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var helper: LecOpenHelper? = null
    private var db: SQLiteDatabase? = null
    var lecId:String?=null
    var intent_classname: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lecinfo)

        lecId = intent.getStringExtra("lecId")

        readData()

        bbs.setOnClickListener {
            val intent = Intent(this@LecInfoActivity, BBSActivity::class.java)
            intent.putExtra("lecId",lecId)
            intent.putExtra("lecname",intent_classname)
            startActivity(intent)
        }
        note.setOnClickListener {
            val intent = Intent(this@LecInfoActivity, NoteActivity::class.java)
            intent.putExtra("lecId",lecId)
            intent.putExtra("lecname",intent_classname)
            startActivity(intent)
        }

        //サブメニュー
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val drawer = findViewById<View>(R.id.drawer_lecinfo_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<View>(R.id.nav_view_lecinfo) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun readData() {
        if (helper == null) {
            helper = LecOpenHelper(applicationContext)
        }

        if (db == null) {
            db = helper!!.readableDatabase
        }
        Log.d("debug", "**********Cursor")
        println("デバッグ："+lecId)
        val cursor = db!!.query(
                "classinfo",
                arrayOf("id", "name","term","week","time","unit","teacher","evaluat","URL"), "id="+lecId, null, null, null, null
        )
        cursor.moveToFirst()
        cursor.moveToPosition(0)

        name.text = cursor.getString(1)
        intent_classname = cursor.getString(1)
        time.text = "時限:"+cursor.getString(3)+cursor.getString(4)
        teacher.text ="講師:"+ cursor.getString(6)
        evaluation.text ="評価:"+ cursor.getString(7)
        val uri:Uri = Uri.parse(cursor.getString(8))
        cursor.close()
        //URLへ飛ぶ
        URL.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        if (drawer_lecinfo_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_lecinfo_layout.closeDrawer(GravityCompat.START)
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

        val drawer = findViewById<View>(R.id.drawer_lecinfo_layout) as? DrawerLayout
        if(drawer != null) {
            drawer.closeDrawer(GravityCompat.START)
        }
        return true
    }

}


