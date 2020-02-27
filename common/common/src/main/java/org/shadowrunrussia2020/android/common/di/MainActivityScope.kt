package org.shadowrunrussia2020.android.common.di

import org.shadowrunrussia2020.android.common.models.HistoryRecord

interface MainActivityScope {
    val router:Router

    interface Router{
        fun goToCastSpellScreen(spellId:String)
        fun showSpellResult(records: Array<HistoryRecord>)
    }

    interface Dependency

    interface Components
}