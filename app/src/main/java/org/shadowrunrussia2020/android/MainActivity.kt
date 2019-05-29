package org.shadowrunrussia2020.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.shadowrunrussia2020.android.qr.Data
import org.shadowrunrussia2020.android.qr.Type
import org.shadowrunrussia2020.android.qr.maybeProcessActivityResult
import org.shadowrunrussia2020.android.qr.startScanQrActivity
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        drawer_layout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (currentFocus != null) {
                    imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
                }
            }
        })

        val navController = findNavController(R.id.nav_host_fragment)
        nav_view.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration.Builder(hashSetOf(R.id.mainFragment, R.id.billingFragment))
            .setDrawerLayout(drawer_layout)
            .build()
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        // TODO(aeremin) Use more Navigation UI & navigation graphs
        // https://developer.android.com/guide/navigation/navigation-migrate
        nav_view.menu.findItem(R.id.scan_qr).setOnMenuItemClickListener {
            startScanQrActivity(this)
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }
        nav_view.menu.findItem(R.id.nav_gallery).setOnMenuItemClickListener {
            val action = MainNavGraphDirections.actionGlobalShowQr(
                Data(Type.DIGITAL_SIGNATURE, 0, (Date().time / 1000).toInt() + 3600, "Petya"))
            navController.navigate(action)
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val qrData = maybeProcessActivityResult(this, requestCode, resultCode, data)
        if (qrData != null) {
            Toast.makeText(this, "Содержимое QR-кода: ${qrData.type}, ${qrData.payload}", Toast.LENGTH_LONG).show()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(findNavController(R.id.nav_host_fragment), appBarConfiguration)
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
        CoroutineScope(IO).launch { (application as ShadowrunRussia2020Application).getDatabase().clearAllTables() }
        // TODO(aeremin) Add equivalent of this.stopService(Intent(this, BeaconsScanner::class.java))
        val intent = Intent(this, LoginActivity::class.java)
        finishAffinity()
        startActivity(intent)
    }
}
