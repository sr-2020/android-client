package org.shadowrunrussia2020.android.common.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

class UserResponse(
    var id: Int,
    var location: Location?
) {
    class Location(
        var label: String
    )
}

@Entity
data class Position(
    @PrimaryKey
    val id: Int,
    val location: String
)

fun fromResponse(r: UserResponse): Position {
    val l = r.location
    val location = l?.label ?: "None"

    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    format.timeZone = TimeZone.getTimeZone("UTC")
    return Position(r.id, location)
}
