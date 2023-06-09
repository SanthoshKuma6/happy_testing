package com.edu.happytesting.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class HappyClass:Application() {
    /**
     * Base class for maintaining global application state. You can provide your own implementation by creating a subclass
     * and specifying the fully-qualified name of this subclass as the "android:name" attribute in your AndroidManifest.xml
     * <application> tag. The Application class, or your subclass of the Application class, is instantiated before any other
     * class when the process for your application/package is created.
     */


    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var getContext: Context
    }

    override fun onCreate() {
        super.onCreate()

        //getContext - is used in the application level like we used in retrofit initialization
        getContext = this
    }
}