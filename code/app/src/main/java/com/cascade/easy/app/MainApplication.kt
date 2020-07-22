package com.cascade.easy.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.content.res.Resources
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.cascade.easy.BuildConfig
import com.cascade.easy.activity.MainActivity
import com.cascade.easy.activity.base.BaseActivity
import com.cascade.easy.data.MainContent
import com.cascade.easy.data.SettingsPreference
import com.cascade.easy.manager.NetworkManager
import com.cascade.easy.manager.PreferenceManager
import com.cascade.easy.model.network.request.Error
import com.cascade.easy.model.preference.Settings
import com.cascade.easy.network.MockServer
import java.util.*

class MainApplication : Application(), Application.ActivityLifecycleCallbacks {
    val preferenceService by lazy { PreferenceManager(this) }

    override fun attachBaseContext(base: Context) {
        initializePreferences(base)
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.MOCK_SERVER_ENABLED) {
            MockServer.start(assets)
        }

        initializeExceptionHandler()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            initializeShortcuts()
        }
        registerActivityLifecycleCallbacks(this)
    }

    override fun onTerminate() {
        if (BuildConfig.MOCK_SERVER_ENABLED) {
            MockServer.shutdown()
        }

        super.onTerminate()
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityDestroyed(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activity.packageName == packageName && activity !is BaseActivity) {
            throw IllegalStateException("Activity must be inherited from BaseActivity class!")
        }
    }

    override fun onActivityResumed(activity: Activity) {}

    private fun initializePreferences(base: Context) = with(PreferenceManager(base)) {
        provideDefaultValues(hashMapOf(SettingsPreference to createDefaultSettings()))

        getPreference(SettingsPreference)?.let {
            val locale = Locale.forLanguageTag(it.language)
            Locale.setDefault(locale)
        }
    }

    private fun initializeExceptionHandler() {
        val defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()!!
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            NetworkManager.NETWORK_SERVICE.logError(Error(e.message!!)).send()
            defaultExceptionHandler.uncaughtException(t, e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun initializeShortcuts() {
        val shortcutManager = getSystemService(ShortcutManager::class.java) ?: return

        val shortcuts = MainContent.values().map {
            val label = getString(it.titleRes)
            val icon = Icon.createWithResource(this, it.iconRes)
            val intent = Intent(this, MainActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                putExtras(MainActivity.createOptions(it))
            }

            ShortcutInfo.Builder(this, label)
                .setShortLabel(label)
                .setIcon(icon)
                .setIntent(intent)
                .build()
        }

        shortcutManager.dynamicShortcuts = shortcuts
    }

    private fun createDefaultSettings(): Settings {
        val systemLanguage = with(Resources.getSystem().configuration) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locales.get(0).language
            } else {
                @Suppress("DEPRECATION")
                locale.language
            }
        }
        val defaultLanguage = Configuration.SUPPORTED_LANGUAGES.let {
            it.forEach { language ->
                if (language == systemLanguage) {
                    return@let language
                }
            }
            return@let Configuration.DEFAULT_LANGUAGE
        }
        return Settings(defaultLanguage)
    }
}