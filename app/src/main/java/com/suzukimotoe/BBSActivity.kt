package com.suzukimotoe

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_bbs.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_userinfo.*


class BBSActivity  : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {

    var lecId:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bbs)

        lecId = intent.getStringExtra("lecId")
        val intent_classname = intent.getStringExtra("lecname")

        //授業名
        lesson.setText(intent_classname)
    
        lecinfo.setOnClickListener {
            val intent = Intent(this@BBSActivity, LecInfoActivity::class.java)
            intent.putExtra("lecId",lecId)
            startActivity(intent)
        }
        
        note.setOnClickListener {
            val intent = Intent(this@BBSActivity, NoteActivity::class.java)
            intent.putExtra("lecId",lecId)
            intent.putExtra("lecname",intent_classname)
            startActivity(intent)
        }

        val max=30
        val dataArray: Array<String?> = arrayOfNulls(max)
        var x = 0
        var commentNumber = 0
        dataArray.fill("")

        val database = FirebaseDatabase.getInstance()

        var key = "coment"
        var ref = database.getReference(key)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.childrenCount>max){
                    commentNumber = max
                }else{
                    commentNumber = dataSnapshot.childrenCount.toInt()
                }
                while (x < commentNumber) {
                    val value = dataSnapshot.child((dataSnapshot.childrenCount.toInt()-commentNumber+x).toString()).value
                    dataArray[x] = value.toString()

                    var adapter = ArrayAdapter(this@BBSActivity, android.R.layout.simple_list_item_1, dataArray)
                    ListView.adapter = adapter
                    x++
                }
                key = (dataSnapshot.childrenCount).toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("onCancelled", "error:", error.toException())
            }
        })
        input.setOnClickListener {
            ref.child(key).setValue(coment.text.toString())
        }

        //サブメニュー
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val drawer = findViewById<View>(R.id.drawer_bbs_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<View>(R.id.nav_view_bbs) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_bbs_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_bbs_layout.closeDrawer(GravityCompat.START)
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