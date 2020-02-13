package org.shadowrunrussia2020.android.di

import org.shadowrunrussia2020.android.common.di.MainActivityScope
import org.shadowrunrussia2020.android.main.ActivityScopeMainDependency
import org.shadowrunrussia2020.android.settings.ActivityScopeSettingsDependency

interface IMainActivityDi:
    MainActivityScope,
    ActivityScopeSettingsDependency,
    ActivityScopeMainDependency


