package org.shadowrunrussia2020.android.positioning

class PositionsResponse(
    var beacons: List<BeaconData>,
    var routers: List<String>,
    var created_at: String,
    var id: Int,
    var user_id: Int
)
