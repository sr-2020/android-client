package org.shadowrunrussia2020.android.common.di

interface MainActivityScope {
    val router:Router

    interface Router{
        fun goToCastSpellScreen(spellId:String)
    }

    interface Dependency

    interface Components
}