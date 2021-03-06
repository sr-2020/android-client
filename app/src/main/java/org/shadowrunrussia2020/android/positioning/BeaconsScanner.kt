package org.shadowrunrussia2020.android.positioning

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.google.common.collect.EvictingQueue
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.altbeacon.beacon.*
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.models.BeaconDataModel
import org.shadowrunrussia2020.android.common.models.PositionsRequest
import java.io.IOException


class BeaconsScanner : Service(), BeaconConsumer {
    private val TAG = "ComConBeacons"
    private lateinit var mBeaconManager: BeaconManager
    private val positionsRepository by lazy { ApplicationSingletonScope.ComponentProvider.components.positionsRepository }

    val disposer = CompositeDisposable()

    private val firestore = FirebaseFirestore.getInstance()

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
        mBeaconManager.foregroundBetweenScanPeriod = 7000 // 7 seconds
        mBeaconManager.foregroundScanPeriod = 3000 // 3 seconds

        mBeaconManager.bind(this)
    }

    override fun onDestroy() {
        disposer.dispose()
        try {
            mBeaconManager.removeAllRangeNotifiers()
            mBeaconManager.stopRangingBeaconsInRegion(Region("myRangingUniqueId", null, null, null))
            mBeaconManager.unbind(this)
            mBeaconManager.disableForegroundServiceScanning()
        } catch (e: RemoteException) {
            Log.e(TAG, "RemoteException: $e")
        }

    }

    val timestamps = EvictingQueue.create<Timestamp>(20)

    override fun onBeaconServiceConnect() {
        Log.d(TAG, "onBeaconServiceConnect")
        val rangeNotifier = { beacons: Collection<Beacon>, _: Region ->
            Log.d(TAG, "didRangeBeaconsInRegion called with beacon count:  " + beacons.size)
            sendBeacons(beacons)
        }
        try {
            mBeaconManager.startRangingBeaconsInRegion(
                Region(
                    "myRangingUniqueId",
                    null,
                    null,
                    null
                )
            )
            mBeaconManager.addRangeNotifier(rangeNotifier)
        } catch (e: RemoteException) {
            Log.e(TAG, "RemoteException: $e")
        }

    }

    private fun sendBeacons(beacons: Collection<Beacon>) {
        var beaconsList = mutableListOf<BeaconDataModel>()
        for (b in beacons) {
            beaconsList.add(BeaconDataModel(b.id1.toString(), b.bluetoothAddress, b.rssi))
        }
        val req = PositionsRequest(beaconsList)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                positionsRepository.sendBeacons(req)
            } catch (e: IOException) {
                Log.e(TAG, "Error while sending beacons to the server: $e");
            }
        }

        val t = Timestamp.now()
        timestamps.add(t)
        for (b in beacons) {
            val beaconsCollection = firestore
                .collection("characters")
                .document(ApplicationSingletonScope.DependencyProvider.dependency.session.getCharacterId().toString())
                .collection("beacons")
            beaconsCollection.add(
                hashMapOf(
                    "timestamp" to t,
                    "mac" to b.bluetoothAddress,
                    "rssi" to b.rssi
                )
            )
        }
        val wakeUpsCollection = firestore
            .collection("characters")
            .document(ApplicationSingletonScope.DependencyProvider.dependency.session.getCharacterId().toString())
            .collection("wakeups")
        wakeUpsCollection.add(
            hashMapOf(
                "timestamp" to t,
                "total_beacons" to beacons.size
            )
        )
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
