package org.shadowrunrussia2020.android

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.billing.BillingViewModel
import org.shadowrunrussia2020.android.character.CharacterViewModel
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.models.Character
import org.shadowrunrussia2020.android.common.models.Position
import org.shadowrunrussia2020.android.common.utils.Data
import org.shadowrunrussia2020.android.common.utils.Type
import org.shadowrunrussia2020.android.common.utils.showErrorMessage
import org.shadowrunrussia2020.android.di.IMainActivityDi
import org.shadowrunrussia2020.android.positioning.BeaconsScanner
import org.shadowrunrussia2020.android.positioning.PositionsViewModel
import java.util.*


class MainActivity : AppCompatActivity(), IMainActivityDi {
    private val appBarConfiguration by lazy {
        AppBarConfiguration.Builder(
            hashSetOf(
                R.id.billingFragment,
                R.id.characterFragmentLegacy,
                R.id.charterMainFragment,
                R.id.spellbookFragment,
                R.id.activeAbilitiesFragment,
                R.id.passiveAbilitiesFragment,
                R.id.spellbookFragment,
                R.id.historyFragment,
                R.id.allPositionsFragment
            )
        )
            .setDrawerLayout(drawer_layout)
            .build()
    }
    private val navController: NavController by lazy { findNavController(R.id.nav_host_fragment) }
    private val PERMISSION_REQUEST_COARSE_LOCATION = 1
    private val REQUEST_ENABLE_BT = 2

    private val characterViewModel by lazy {
        ViewModelProviders.of(this).get(CharacterViewModel::class.java)
    }
    private val billingViewModel by lazy {
        ViewModelProviders.of(this).get(BillingViewModel::class.java)
    }

    private val positionsViewModel by lazy {
        ViewModelProviders.of(this).get(PositionsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        drawer_layout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerStateChanged(newState: Int) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                currentFocus?.let {
                    imm.hideSoftInputFromWindow(it.windowToken, 0)
                }
            }
        })

        nav_view.setupWithNavController(navController)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        nav_view.menu.findItem(R.id.nav_gallery).setOnMenuItemClickListener {
            val characterData = characterViewModel.getCharacter()
            val observer: Observer<Character> = object : Observer<Character> {
                override fun onChanged(character: Character?) {
                    if (character != null) {
                        characterData.removeObserver(this)
                        val action = MainNavGraphDirections.actionGlobalShowQr(
                            Data(
                                Type.DIGITAL_SIGNATURE,
                                0,
                                (Date().time / 1000).toInt() + 3600,
                                character.modelId
                            )
                        )
                        navController.navigate(action)
                        drawer_layout.closeDrawer(GravityCompat.START)
                    }
                }

            }
            characterData.observeForever(observer)
            true
        }

        characterViewModel.getCharacter().observe(this,
            Observer { data: Character? ->
                if (data != null && data.healthState != "healthy" &&
                    navController.currentDestination?.id != R.id.woundedFragment) {
                    navController.navigate(MainNavGraphDirections.actionGlobalWounded())
                }

                if (data != null) {
                    val header = findViewById<NavigationView>(R.id.nav_view).getHeaderView(0);
                    header.findViewById<TextView>(R.id.headerTitle).text = "Персонаж #${data.modelId}"
                    header.findViewById<TextView>(R.id.headerSubtitle).text = ""
                }
            })

        positionsViewModel.positions().observe(this,
            Observer { data: List<Position>? ->
                val myPosition = data?.find { it.id == ApplicationSingletonScope.DependencyProvider.dependency.session.getCharacterId() }
                if (myPosition != null) {
                    toolbar.subtitle = myPosition.location
                }
        })

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.woundedFragment) {
                toolbar.visibility = View.GONE
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            } else {
                toolbar.visibility = View.VISIBLE
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (ApplicationSingletonScope.DependencyProvider.dependency.session.getToken() == null) {
            goToLoginScreen()
        }
    }

    override fun onResume() {
        super.onResume()
        if (ApplicationSingletonScope.DependencyProvider.dependency.session.getToken() == null) {
            return exit()
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                    characterViewModel.refresh()
                    billingViewModel.refresh()
                }
            } catch (e: Exception) {
                showErrorMessage(this@MainActivity, "Ошибка. ${e.message}")
                exit()
            }
        }

        checkEverythingEnabled()
        startService(Intent(this, BeaconsScanner::class.java))
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun checkEverythingEnabled() {
        checkBluetoothEnabled()
        checkLocationPermission()
        checkLocationService()
    }

    private fun checkBluetoothEnabled() {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    private fun checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    PERMISSION_REQUEST_COARSE_LOCATION
                )
            }
        }
    }

    private fun checkLocationService() {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder(this)
                .setMessage(getString(R.string.enable_location))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0)
                }
                .create()
                .show()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_logout -> {
                exit(); return true
            }
            R.id.action_wound -> {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                            characterViewModel.postEvent(
                                "wound"
                            )
                        }
                    } catch (e: Exception) {
                        showErrorMessage(this@MainActivity, "${e.message}")
                    }
                }
                return true
            }
            else -> return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(
                item
            )
        }
    }

    override fun exit() {
        ApplicationSingletonScope.DependencyProvider.dependency.session.invalidate()
        this.stopService(Intent(this, BeaconsScanner::class.java))
        CoroutineScope(Dispatchers.Main).launch {
            withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                ApplicationSingletonScope.ComponentProvider.components.clearAllTables()
                FirebaseInstanceId.getInstance().deleteInstanceId()
            }
            goToLoginScreen()
        }
    }

    private fun goToLoginScreen() {
        // We don't use Android's Navigation Component here because it somehow messes with back stack no matter what
        // we do (i.e. pressing "back" on login screen tries to go to the main screen).
        // Current implementation is taken from
        // https://medium.com/google-developer-experts/using-navigation-architecture-component-in-a-large-banking-app-ac84936a42c2
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
