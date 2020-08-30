package com.ranajit.nearbystore

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import java.lang.ref.WeakReference

class StoreApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        wApp!!.clear()
        wApp = WeakReference(this@StoreApp)

    }

    companion object {
        private var wApp: WeakReference<StoreApp>? =
            WeakReference<StoreApp>(null)!!
        val instance: StoreApp get() = wApp?.get()!!

        val context: Context
            get() {
                val app = if (null != wApp) wApp!!.get() else StoreApp()
                return if (app != null) app.applicationContext else StoreApp().applicationContext
            }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}