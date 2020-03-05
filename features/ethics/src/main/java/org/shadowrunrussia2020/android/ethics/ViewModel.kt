package org.shadowrunrussia2020.android.ethics

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.models.Character
import org.shadowrunrussia2020.android.common.models.CharacterResponse
import org.shadowrunrussia2020.android.common.models.Event
import org.shadowrunrussia2020.android.common.models.HistoryRecord

internal class ViewModel : ViewModel() {

    private val mRepository = ApplicationSingletonScope.DependencyProvider.provideDependency<EthicsScreenDependency>().characterRepository

    val character get(): LiveData<Character> = mRepository.getCharacter()

    suspend fun refresh() = mRepository.refresh()

    suspend fun postEvent(eventType: String, data: HashMap<String, Any> = hashMapOf()): CharacterResponse? =
        mRepository.sendEvent(Event(eventType, data))
}