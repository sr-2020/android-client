package org.shadowrunrussia2020.android

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat.finishAffinity
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.shadowrunrussia2020.android.models.billing.Balance
import org.shadowrunrussia2020.android.models.billing.Transaction
import org.shadowrunrussia2020.android.qr.Data
import org.shadowrunrussia2020.android.qr.Type
import org.shadowrunrussia2020.android.qr.maybeProcessActivityResult
import org.shadowrunrussia2020.android.qr.startScanQrActivity
import java.lang.System.exit
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var mModel: BillingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        mModel = ViewModelProviders.of(this).get(BillingViewModel::class.java)
        val textView2 = findViewById<TextView>(R.id.textView2)
        mModel.getBalance().observe(this, Observer { balance: Int? -> textView2.text = balance.toString() })
        val textView3 = findViewById<TextView>(R.id.textView3)
        mModel.getHistory().observe(this, Observer { h: List<Transaction>? -> textView3.text = h?.size.toString() })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val qrData = maybeProcessActivityResult(this, requestCode, resultCode, data)
        if (qrData != null) {
            Toast.makeText(this, "Содержимое QR-кода: ${qrData.type}, ${qrData.payload}", Toast.LENGTH_LONG).show()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> { mModel.refresh(); return true }
            R.id.action_logout -> { exit(); return true }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("ApplySharedPref")
    private fun exit() {
        val preferences = (application as ShadowrunRussia2020Application).getGlobalSharedPreferences()
        preferences.edit().remove(getString(R.string.token_preference_key)).commit()
        // TODO(aeremin) Add equivalent
        // this.stopService(Intent(this, BeaconsScanner::class.java))
        val intent = Intent(this, LoginActivity::class.java)
        finishAffinity()
        startActivity(intent)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.scan_qr -> startScanQrActivity(this)
            R.id.nav_gallery -> startShowQrActivity(this,
                Data(Type.DIGITAL_SIGNATURE, 0, (Date().time / 1000).toInt() + 3600, "Petya"))
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
