package com.suzukimotoe

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.internal.FirebaseAppHelper.getUid
import kotlinx.android.synthetic.main.activity_userinfo.*
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener


class UserinfoActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        var email:String? =""
        if(user != null){
            email = user.email
        }
        setContentView(R.layout.activity_userinfo)
        val email_text: TextView = findViewById(R.id.user_mail_address)
        email_text.setText(email)
        password_button.setOnClickListener {
            if(email != null) {
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                            }else {
                                Toast.makeText(applicationContext, "メールアドレスが得られませんでした", Toast.LENGTH_SHORT).show()
                            }
                        }
            }

        }

        //サブメニュー
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val drawer = findViewById<View>(R.id.drawer_userinfo_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<View>(R.id.nav_view_userinfo) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_userinfo_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_userinfo_layout.closeDrawer(GravityCompat.START)
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

            }
            R.id.nav_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(application, LoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }

        val drawer = findViewById<View>(R.id.drawer_userinfo_layout) as? DrawerLayout
        if(drawer != null) {
            drawer.closeDrawer(GravityCompat.START)
        }
        return true
    }

}
