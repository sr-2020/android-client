package org.shadowrunrussia2020.android.abilities

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class ActiveAbilityUseViewModel(application: Application) : AndroidViewModel(application) {
    val data = hashMapOf<String, Any>()
}