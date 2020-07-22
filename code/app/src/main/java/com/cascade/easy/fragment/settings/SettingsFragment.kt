package com.cascade.easy.fragment.settings

import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.app.ActivityCompat
import com.cascade.easy.R
import com.cascade.easy.app.Configuration
import com.cascade.easy.data.NightMode
import com.cascade.easy.data.SettingsPreference
import com.cascade.easy.fragment.base.BaseFragment
import com.cascade.easy.model.preference.Settings
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.*

class SettingsFragment : BaseFragment() {
    private val settings get() = preferenceService.getPreference(SettingsPreference)!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeSettings()
    }

    private fun updateSettings(newSettings: Settings) {
        if (newSettings != settings) {
            preferenceService.setPreference(SettingsPreference, newSettings)
        }
    }

    private fun setupLanguageSettings() {
        Configuration.SUPPORTED_LANGUAGES.map {
            Pair(it, Locale.forLanguageTag(it))
        }.forEach {
            val locale = it.second
            AppCompatRadioButton(context).apply {
                id = View.generateViewId()
                tag = it.first
                textLocale = locale
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val languageTag = locale.toLanguageTag()
                    textLocales = LocaleList.forLanguageTags(languageTag)
                }
                text = it.second.getDisplayLanguage(it.second)
                isChecked = it.first == settings.language
            }.let { button ->
                radioGroupLanguages.addView(button)
            }
        }
        radioGroupLanguages.setOnCheckedChangeListener { group, checkedId ->
            val radioButtonLanguage = group.findViewById<RadioButton>(checkedId)
            val selectedLanguage = radioButtonLanguage.tag as String

            val newSettings = settings.copy(language = selectedLanguage)
            updateSettings(newSettings)

            ActivityCompat.recreate(activity!!)
        }
    }

    private fun setupNightModeSettings() {
        NightMode.values().forEach {
            AppCompatRadioButton(context).apply {
                id = View.generateViewId()
                tag = it
                text = getText(it.labelRes)
                isChecked = it == settings.nightMode
            }.let {
                radioGroupNightModes.addView(it)
            }
        }
        radioGroupNightModes.setOnCheckedChangeListener { group, checkedId ->
            val radioButtonNightMode = group.findViewById<RadioButton>(checkedId)
            val nightMode = radioButtonNightMode.tag as NightMode

            val newSettings = settings.copy(nightMode = nightMode)
            updateSettings(newSettings)

            AppCompatDelegate.setDefaultNightMode(nightMode.systemValue)
        }
    }

    private fun initializeSettings() {
        setupLanguageSettings()
        setupNightModeSettings()
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}