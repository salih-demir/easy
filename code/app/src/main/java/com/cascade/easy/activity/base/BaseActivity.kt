package com.cascade.easy.activity.base

import android.annotation.SuppressLint
import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.cascade.easy.app.AppModule
import com.cascade.easy.app.MainApplication
import com.cascade.easy.data.SettingsPreference
import com.cascade.easy.fragment.base.BaseFragment
import com.cascade.easy.lang.lifecycle
import com.cascade.easy.manager.NetworkManager
import com.cascade.easy.manager.PreferenceManager
import com.cascade.easy.manager.SpeechManager
import com.cascade.easy.util.createConfigurationForLocale
import java.util.*

@SuppressLint("Registered")
abstract class BaseActivity : AppCompatActivity(), AppModule, TextToSpeech.OnInitListener {
    override val preferenceService by lazy { mainApplication.preferenceService }
    override val networkService by lazy { NetworkManager.NETWORK_SERVICE }
    override val speechService by lifecycle(lifecycle) { SpeechManager(this, this) }

    private val mainApplication by lazy { application as MainApplication }

    override fun attachBaseContext(newBase: Context) {
        initializePreferences(newBase)
        super.attachBaseContext(newBase)
    }

    override fun provideContext() = this

    override fun onDestroy() {
        speechService.shutdown()

        super.onDestroy()
    }

    override fun onAttachFragment(fragment: Fragment) {
        val isInSamePackage = fragment.javaClass.`package`!!.name.contains(packageName)
        val isBaseFragment = fragment is BaseFragment
        if (isInSamePackage && !isBaseFragment) {
            throw IllegalStateException("Fragment must be inherited from BaseFragment class!")
        }

        super.onAttachFragment(fragment)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locale = Locale.getDefault()
            if (speechService.isLanguageAvailable(locale) == TextToSpeech.LANG_AVAILABLE) {
                speechService.language = locale
            } else {
                speechService.shutdown()
            }
        }
    }

    // Set the application language based on the settings and override context configuration with new locale.
    private fun initializePreferences(baseContext: Context) {
        PreferenceManager(baseContext).getPreference(SettingsPreference)?.let {
            AppCompatDelegate.setDefaultNightMode(it.nightMode.systemValue)

            val locale = Locale.forLanguageTag(it.language)
            Locale.setDefault(locale)

            val configuration = baseContext.createConfigurationForLocale(locale)
            applyOverrideConfiguration(configuration)
        }
    }
}