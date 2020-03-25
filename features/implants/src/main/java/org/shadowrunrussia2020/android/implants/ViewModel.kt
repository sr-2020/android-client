package org.shadowrunrussia2020.android.implants

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.models.Character

internal class ViewModel : ViewModel() {

    private val mRepository = ApplicationSingletonScope.DependencyProvider.provideDependency<ImplantScreensDependency>().characterRepository

    val character get(): LiveData<Character> = mRepository.getCharacter()

    suspend fun refresh() = mRepository.refresh()
}