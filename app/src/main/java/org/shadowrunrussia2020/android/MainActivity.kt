package org.shadowrunrussia2020.android

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.shadowrunrussia2020.android.qr.Data
import org.shadowrunrussia2020.android.qr.Type
import org.shadowrunrussia2020.android.qr.maybeProcessActivityResult
import org.shadowrunrussia2020.android.qr.startScanQrActivity
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var appBarConfiguration: AppBarConfiguration

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

        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawer_layout)
        nav_view.setupWithNavController(navController)
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
            R.id.action_settings -> { return true }
            R.id.action_logout -> { exit(); return true }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun exit() {
        (application as ShadowrunRussia2020Application).getSession().invalidate()
        // TODO(aeremin) Add equivalent of this.stopService(Intent(this, BeaconsScanner::class.java))
        val intent = Intent(this, LoginActivity::class.java)
        finishAffinity()
        startActivity(intent)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        if (item.onNavDestinationSelected(navController)) {
            return true
        }

        when (item.itemId) {
            R.id.scan_qr -> startScanQrActivity(this)
            R.id.nav_gallery -> startShowQrActivity(this,
                Data(Type.DIGITAL_SIGNATURE, 0, (Date().time / 1000).toInt() + 3600, "Petya"))
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
