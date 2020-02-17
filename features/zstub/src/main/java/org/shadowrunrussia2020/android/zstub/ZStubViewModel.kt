package org.shadowrunrussia2020.android.zstub

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.models.Character
import org.shadowrunrussia2020.android.common.models.CharacterResponse
import org.shadowrunrussia2020.android.common.models.Event
import org.shadowrunrussia2020.android.common.models.HistoryRecord

internal class ZStubViewModel : ViewModel() {

    private val dependency: ZStubScreenDependency = ApplicationSingletonScope.DependencyProvider.provideDependency()

}