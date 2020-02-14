package org.shadowrunrussia2020.android.common.models

class PositionsRequest(var beacons: List<BeaconDataModel>)

class PositionsResponse(
    var beacons: List<BeaconDataModel>,
    var routers: List<String>,
    var created_at: String,
    var id: Int,
    var user_id: Int
)

class BeaconDataModel(var ssid: String, var bssid: String, var level: Int)

