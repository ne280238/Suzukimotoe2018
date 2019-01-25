package com.suzukimotoe

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_note.*
import java.util.*
import android.support.annotation.NonNull
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.activity_bbs.*


class NoteActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {

    private val classnames = arrayOf(
            "第1回授業", "第2回授業", "第3回授業", "第4回授業",
            "第5回授業", "第6回授業", "第7回授業", "第8回授業",
            "第9回授業", "第10回授業", "第11回授業", "第12回授業",
            "第13回授業", "第14回授業", "第15回授業", "その他"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        val lecId: String = intent.getStringExtra("lecId")
        val intent_classname = intent.getStringExtra("lecname")

        //授業名
        val classname_name: TextView = findViewById(R.id.classname2)
        classname_name.setText(intent_classname)

        //リスト
        val listview: ListView = findViewById(R.id.classname_list)

        val adapter =ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                classnames
        )

        listview.adapter = adapter

        listview.setOnItemClickListener{_,_,position,_ ->
            val intent = Intent(application, com.suzukimotoe.ListActivity::class.java)
            val selectedText: String = classnames[position]
            val selectedid: Int = position + 1
            intent.putExtra("Text",selectedText)
            intent.putExtra("class",intent_classname)
            intent.putExtra("class_id",selectedid)
            intent.putExtra("classid",lecId.toInt())
            startActivity(intent)
        }

        //講義情報Activityへ
        val lecinfo: Button = findViewById(R.id.lecinfo)
        lecinfo.setOnClickListener {
            val intent = Intent(application, LecInfoActivity::class.java)
            intent.putExtra("lecId",lecId)
            startActivity(intent)
        }

        //BBSActivityへ
        val bbs: Button = findViewById(R.id.bbs)
        bbs.setOnClickListener {
            val intent = Intent(application, BBSActivity::class.java)
            intent.putExtra("lecId",lecId)
            intent.putExtra("lecname",intent_classname)
            startActivity(intent)
        }

        //サブメニュー
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val drawer = findViewById<View>(R.id.drawer_note_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<View>(R.id.nav_view_note) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_note_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_note_layout.closeDrawer(GravityCompat.START)
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

        val drawer = findViewById<View>(R.id.drawer_note_layout) as? DrawerLayout
        if(drawer != null) {
            drawer.closeDrawer(GravityCompat.START)
        }
        return true
    }

}







