package org.shadowrunrussia2020.android

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.altbeacon.beacon.*
import org.shadowrunrussia2020.android.character.CharacterRepository
import org.shadowrunrussia2020.android.character.CharacterWebService
import org.shadowrunrussia2020.android.character.models.Event


class BeaconsScanner : Service(), BeaconConsumer {
    private val TAG = "ComConBeacons"
    // private lateinit var mBackgroundPowerSaver: BackgroundPowerSaver
    private lateinit var mBeaconManager: BeaconManager
    private val mRepository by lazy {
        CharacterRepository(
            (application as ShadowrunRussia2020Application).getRetrofit().create(CharacterWebService::class.java),
            (application as ShadowrunRussia2020Application).getDatabase().characterDao()
        )
    }

    override fun onCreate() {
        Log.d(TAG, "BeaconsScanner::onCreate")
        setUpBeaconManager()
    }

    private fun setUpBeaconManager() {
        // Differentiate between beacons with equal payload, but different MAC address.
        Beacon.setHardwareEqualityEnforced(true)

        mBeaconManager = BeaconManager.getInstanceForApplication(this)
        mBeaconManager.beaconParsers.clear()
        // Example advertising data of ble_app_beacon nRF example
        //  0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24
        // 59 00 02 15 01 12 23 34 45 56 67 78 89 9A AB BC CD DE EF F0 01 02 03 04 C3
        // Example chinese beacon
        // 59 00 02 15 01 02 03 04 C3
        // 0x0059 is Nordic Semiconductors manufacturer code.
        var parser = BeaconParser().setBeaconLayout("m:0-3=59000215,i:5-5,i:6-6,i:7-7,p:8-8");
        parser.setHardwareAssistManufacturerCodes(intArrayOf(0x59))
        mBeaconManager.beaconParsers.add(parser)

        // Run full cycle every minute
        mBeaconManager.foregroundBetweenScanPeriod = 57000 // 57 seconds
        mBeaconManager.foregroundScanPeriod = 3000 // 3 seconds

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = Notification.Builder(this)
            .setSmallIcon(R.drawable.abc_ic_star_black_48dp)
            .setContentTitle("Приложение висит в фоне!")
            .setContentIntent(pendingIntent)
            .setGroup("beacons")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "My Notification Channel ID",
                "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "My Notification Channel Description"
            val notificationManager = getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            builder.setChannelId(channel.id)
        }
        mBeaconManager.enableForegroundServiceScanning(builder.build(), 1713)
        mBeaconManager.bind(this)

        // mBackgroundPowerSaver = BackgroundPowerSaver(this)
    }

    override fun onDestroy() {
        try {
            mBeaconManager.removeAllRangeNotifiers()
            mBeaconManager.stopRangingBeaconsInRegion(Region("myRangingUniqueId", null, null, null))
            mBeaconManager.unbind(this)
            mBeaconManager.disableForegroundServiceScanning()
        } catch (e: RemoteException) {
            Log.e(TAG, "RemoteException: $e")
        }

    }

    override fun onBeaconServiceConnect() {
        Log.d(TAG, "onBeaconServiceConnect")
        val rangeNotifier = { beacons: Collection<Beacon>, _: Region ->
            Log.d(TAG, "didRangeBeaconsInRegion called with beacon count:  " + beacons.size)
            sendBeacons(beacons)
        }
        try {
            mBeaconManager.startRangingBeaconsInRegion(Region("myRangingUniqueId", null, null, null))
            mBeaconManager.addRangeNotifier(rangeNotifier)
        } catch (e: RemoteException) {
            Log.e(TAG, "RemoteException: $e")
        }

    }

    private fun sendBeacons(beacons: Collection<Beacon>) {
        for (b in beacons) {
            Log.d(TAG, "Beacon id = " + b.id1 + "-" + b.id2 + "-" + b.id3 + " RSSI = " + b.rssi)
            if (b.rssi > -50) {
                CoroutineScope(Dispatchers.IO).launch { mRepository.sendEvent(Event("dummy-spell")) }
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
