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
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.billing.BillingViewModel
import org.shadowrunrussia2020.android.character.CharacterViewModel
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.models.Character
import org.shadowrunrussia2020.android.common.models.HealthState
import org.shadowrunrussia2020.android.common.models.Position
import org.shadowrunrussia2020.android.common.utils.MainThreadSchedulers
import org.shadowrunrussia2020.android.common.utils.plusAssign
import org.shadowrunrussia2020.android.common.utils.showErrorMessage
import org.shadowrunrussia2020.android.common.utils.showSuccessMessage
import org.shadowrunrussia2020.android.di.IMainActivityDi
import org.shadowrunrussia2020.android.model.qr.qrData
import org.shadowrunrussia2020.android.positioning.BeaconsScanner
import org.shadowrunrussia2020.android.positioning.PositionsViewModel
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), IMainActivityDi {
    private val appBarConfiguration by lazy {
        AppBarConfiguration.Builder(
            hashSetOf(
                R.id.billingFragment,
                R.id.charterMainFragment,
                R.id.spellbookFragment,
                R.id.activeAbilitiesFragment,
                R.id.passiveAbilitiesFragment,
                R.id.buyForKarmaFragment,
                R.id.ethicsFragment,
                R.id.implantsFragment,
                R.id.historyFragment,
                R.id.timersFragment,
                R.id.allPositionsFragment
            )
        )
            .setDrawerLayout(drawer_layout)
            .build()
    }
    private val navController: NavController by lazy { findNavController(R.id.nav_host_fragment) }
    private val REQUEST_ENABLE_BT = 2
    private val PERMISSION_REQUEST_FINE_LOCATION = 4

    private val characterViewModel by lazy {
        ViewModelProviders.of(this).get(CharacterViewModel::class.java)
    }
    private val billingViewModel by lazy {
        ViewModelProviders.of(this).get(BillingViewModel::class.java)
    }

    private val positionsViewModel by lazy {
        ViewModelProviders.of(this).get(PositionsViewModel::class.java)
    }

    private val disposer = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        drawer_layout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerStateChanged(newState: Int) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                currentFocus?.let {
                    imm.hideSoftInputFromWindow(it.windowToken, 0)
                }
            }
        })

        swipeRefreshLayout.setOnRefreshListener { refreshData(true) }

        nav_view.setupWithNavController(navController)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        nav_view.menu.findItem(R.id.nav_gallery).setOnMenuItemClickListener {
            val characterData = characterViewModel.getCharacter()
            val observer: Observer<Character> = object : Observer<Character> {
                override fun onChanged(character: Character?) {
                    if (character != null) {
                        characterData.removeObserver(this)
                        val action = MainNavGraphDirections.actionGlobalShowQr(
                            character.qrData
                        )
                        navController.navigate(action)
                        drawer_layout.closeDrawer(GravityCompat.START)
                    }
                }

            }
            characterData.observeForever(observer)
            true
        }

        disposer += Observable.interval(0, 60, TimeUnit.SECONDS)
            .observeOn(MainThreadSchedulers.androidUiScheduler)
            .subscribe { refreshData(false) }

        characterViewModel.getCharacter().observe(this,
            Observer { data: Character? ->
                if (data == null) return@Observer

                val header = findViewById<NavigationView>(R.id.nav_view).getHeaderView(0);
                header.findViewById<TextView>(R.id.headerTitle).text = "Персонаж #${data.modelId}"
                header.findViewById<TextView>(R.id.headerSubtitle).text = "Имя: ${data.name}"

                if (data.healthState == HealthState.healthy) {
                    if (navController.currentDestination?.id == R.id.woundedFragment) {
                        navController.navigate(MainNavGraphDirections.actionGlobalCharacter())
                    }
                } else {
                    if (navController.currentDestination?.id != R.id.woundedFragment) {
                        navController.navigate(MainNavGraphDirections.actionGlobalWounded())
                    }
                }

                val drawerActionToAvailability = mapOf(
                    R.id.action_global_billing to data.screens.billing,
                    R.id.action_global_spellbook to data.screens.spellbook,
                    R.id.action_global_active_abilities to data.screens.activeAbilities,
                    R.id.action_global_passive_abilities to data.screens.passiveAbilities,
                    R.id.action_global_buy_for_karma to data.screens.karma,
                    R.id.action_global_implants to data.screens.implants,
                    R.id.action_global_autodoc to data.screens.autodoc,
                    R.id.action_global_ethics to data.screens.ethics,
                    R.id.action_global_positions to data.screens.location
                )

                val fragmentToAction = mapOf(
                    R.id.billingFragment to R.id.action_global_billing,
                    R.id.spellbookFragment to R.id.action_global_spellbook,
                    R.id.activeAbilitiesFragment to R.id.action_global_active_abilities,
                    R.id.passiveAbilitiesFragment to R.id.action_global_passive_abilities,
                    R.id.buyForKarmaFragment to R.id.action_global_buy_for_karma,
                    R.id.implantsFragment to R.id.action_global_implants,
                    R.id.autodocFragment to R.id.action_global_autodoc,
                    R.id.ethicsFragment to R.id.action_global_ethics,
                    R.id.allPositionsFragment to R.id.action_global_positions
                )

                val toolbarActionToAvailability = mapOf(
                    R.id.action_wound to data.screens.wound,
                    R.id.action_global_scan_qr to data.screens.scanQr
                )

                val currentDestination = navController.currentDestination!!.id
                if (fragmentToAction.containsKey(currentDestination) && !drawerActionToAvailability[fragmentToAction.get(
                        currentDestination
                    )]!!
                ) {
                    navController.navigate(MainNavGraphDirections.actionGlobalCharacter())
                }

                for (entry in drawerActionToAvailability) {
                    drawer_layout.nav_view.menu.findItem(entry.key)?.isVisible = entry.value
                }

                for (entry in toolbarActionToAvailability) {
                    toolbar.menu.findItem(entry.key)?.isVisible = entry.value
                }
            })

        positionsViewModel.positions().observe(this,
            Observer { data: List<Position>? ->
                val myPosition =
                    data?.find { it.id == ApplicationSingletonScope.DependencyProvider.dependency.session.getCharacterId() }
                if (myPosition != null && navController.currentDestination!!.id != R.id.buyForKarmaFragment) {
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

            toolbar.menu.findItem(R.id.action_help)?.isVisible =
                destination.id == R.id.ethicsFragment
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposer.clear()
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

        refreshData(false)
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
        if (BluetoothAdapter.getDefaultAdapter() != null && !BluetoothAdapter.getDefaultAdapter().isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    private fun checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ),
                        PERMISSION_REQUEST_FINE_LOCATION
                    )
                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Местоположение работать не будет")
                    builder.setMessage("Доступ к местоположению не был выдан, поэтому приложение не будет видеть маяки. Пожалуйста, выдайте доступ к местоположению / location в Настройки -> Приложения -> Разрешения / Settings -> Applications -> Permissions.")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener { }
                    builder.show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_FINE_LOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Местоположение работать не будет")
                    builder.setMessage("Доступ к местоположению не был выдан, поэтому приложение не будет видеть маяки. Пожалуйста, выдайте доступ к местоположению / location в Настройки -> Приложения -> Разрешения / Settings -> Applications -> Permissions.")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener { }
                    builder.show()
                }
                return
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
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                exit(); return true
            }
            R.id.action_wound -> {
                wound(); return true
            }
            else -> return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(
                item
            )
        }
    }

    fun setPullToRefreshEnabled(on: Boolean) {
        swipeRefreshLayout.isEnabled = on
    }

    private fun refreshData(interactive: Boolean) {
        if (ApplicationSingletonScope.DependencyProvider.dependency.session.getToken() == null) {
            goToLoginScreen()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                characterViewModel.refresh()
                billingViewModel.refresh()
                if (interactive) {
                    showSuccessMessage(this@MainActivity, "Данные успешно обновлены.")
                }
            } catch (e: Exception) {
                showErrorMessage(this@MainActivity, "${e.message}")
            }
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun exit() {
        ApplicationSingletonScope.DependencyProvider.dependency.session.invalidate()
        this.stopService(Intent(this, BeaconsScanner::class.java))
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                ApplicationSingletonScope.ComponentProvider.components.clearAllTables()
                FirebaseInstanceId.getInstance().deleteInstanceId()
            }
            goToLoginScreen()
        }
    }

    fun wound() {
        AlertDialog.Builder(this)
            .setTitle("Подтверждаете тяжелое ранение?")
            .setPositiveButton(android.R.string.ok) { _, _ ->
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        characterViewModel.postEvent("wound")
                    } catch (e: Exception) {
                        showErrorMessage(this@MainActivity, "${e.message}")
                    }
                }
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .show()
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
