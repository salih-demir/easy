package com.cascade.easy.service

import com.cascade.easy.data.Preference

interface PreferenceService {
    fun <T : Any> getPreference(preference: Preference<T>): T?
    fun <T : Any> setPreference(preference: Preference<T>, value: T)
    fun provideDefaultValues(map: HashMap<Preference<*>, *>)

    fun addPreferenceListener(preferenceListener: PreferenceListener)
    fun removePreferenceListener(preferenceListener: PreferenceListener)
}

interface PreferenceListener {
    fun onPreferenceChanged(preference: Preference<*>)
}