package org.shadowrunrussia2020.android.common.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

class UserResponse(
    var id: Int,
    var name: String?,
    var updated_at: String,
    var location_updated_at: String,
    var location: Location?
) {
    class Location(
        var id: Int,
        var label: String
    )
}

@Entity
data class Position(
    @PrimaryKey
    val id: Int,
    val username: String,
    val location: String,
    val date: Date
)

fun fromResponse(r: UserResponse): Position {
    val l = r.location
    val location = l?.label ?: "None"

    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    format.timeZone = TimeZone.getTimeZone("UTC")
    val date =
        format.parse(if (r.location_updated_at.length > 0) r.location_updated_at else r.updated_at)

    return Position(r.id, r.name ?: "Anonymous", location, date)
}
