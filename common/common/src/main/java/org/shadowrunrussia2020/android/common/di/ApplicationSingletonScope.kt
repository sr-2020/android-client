package org.shadowrunrussia2020.android.common.di

import org.shadowrunrussia2020.android.common.Session
import retrofit2.Retrofit

interface ApplicationSingletonScope {
    val retrofit: Retrofit
    val session: Session
//    val database: CacheDatabase
}