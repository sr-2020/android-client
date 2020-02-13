package org.shadowrunrussia2020.android.settings

import org.shadowrunrussia2020.android.common.di.MainActivityScope

interface ActivityScopeSettingsDependency : MainActivityScope {
    fun exit()
}