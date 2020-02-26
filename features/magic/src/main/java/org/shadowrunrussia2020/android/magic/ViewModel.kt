package org.shadowrunrussia2020.android.magic

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.models.Character
import org.shadowrunrussia2020.android.common.models.CharacterResponse
import org.shadowrunrussia2020.android.common.models.Event
import org.shadowrunrussia2020.android.common.models.HistoryRecord
import org.shadowrunrussia2020.android.common.utils.map

internal class MagicViewModel : ViewModel() {

    private val dependency: MagicScreenDependency = ApplicationSingletonScope.DependencyProvider.provideDependency()

    private val mRepository = dependency.characterRepository

    val character get(): LiveData<Character> = mRepository.getCharacter()

    suspend fun refresh() = mRepository.refresh()

    suspend fun postEvent(eventType: String, data: HashMap<String, Any> = hashMapOf()): CharacterResponse? =
        mRepository.sendEvent(Event(eventType, data))

}