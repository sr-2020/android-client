package org.shadowrunrussia2020.android.common.utils

import androidx.annotation.MainThread
import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

@MainThread
fun <X, Y> LiveData<X>.map(mapFunction: (X?)-> Y): LiveData<Y> = Transformations.map(this, mapFunction)

@MainThread
fun <X, Y> LiveData<X>.switchMap(switchMapFunction:  (X?)->LiveData<Y>): LiveData<Y?> = Transformations.switchMap(this, switchMapFunction)

@MainThread
fun <X> LiveData<X>.distinctUntilChanged(): LiveData<X?> = Transformations.distinctUntilChanged(this)