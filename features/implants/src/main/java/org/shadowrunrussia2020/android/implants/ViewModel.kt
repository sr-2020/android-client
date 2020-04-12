package org.shadowrunrussia2020.android.implants

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.models.Character
import org.shadowrunrussia2020.android.common.models.CharacterResponse
import org.shadowrunrussia2020.android.common.models.Event

internal class ViewModel : ViewModel() {

    private val mRepository =
        ApplicationSingletonScope.DependencyProvider.provideDependency<ImplantScreensDependency>().characterRepository

    val character get(): LiveData<Character> = mRepository.getCharacter()

    suspend fun refresh() = mRepository.refresh()

    suspend fun riggerHeal(targetCharacterId: String): CharacterResponse? {
        return mRepository.sendEvent(
            Event(
                "riggerHeal",
                hashMapOf("targetCharacterId" to targetCharacterId)
            )
        )
    }

    suspend fun riggerRevive(targetCharacterId: String): CharacterResponse? {
        return mRepository.sendEvent(
            Event(
                "riggerRevive",
                hashMapOf("targetCharacterId" to targetCharacterId)
            )
        )
    }
}