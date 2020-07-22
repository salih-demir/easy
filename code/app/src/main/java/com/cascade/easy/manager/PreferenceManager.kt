package com.cascade.easy.manager

import android.content.Context
import android.content.SharedPreferences
import com.cascade.easy.data.Preference
import com.cascade.easy.service.PreferenceListener
import com.cascade.easy.service.PreferenceService
import com.google.gson.Gson

class PreferenceManager(context: Context, preferenceName: String = DEFAULT_PREFERENCE) :
    PreferenceService,
    SharedPreferences.OnSharedPreferenceChangeListener {
    private val gson = Gson()
    private val sharedPreferences =
        context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)

    private var listeners = ArrayList<PreferenceListener>()

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun <T : Any> getPreference(preference: Preference<T>): T? = with(sharedPreferences) {
        getString(preference.key, null)?.let {
            gson.fromJson(it, preference.mClass.java) as T
        }
    }

    override fun <T : Any> setPreference(preference: Preference<T>, value: T) =
        with(sharedPreferences.edit()) {
            putString(preference.key, gson.toJson(value))
            apply()
        }

    override fun provideDefaultValues(map: HashMap<Preference<*>, *>) =
        with(sharedPreferences.edit()) {
            map.forEach {
                if (!sharedPreferences.contains(it.key.key)) {
                    putString(it.key.key, gson.toJson(it.value))
                }
            }
            apply()
        }

    override fun addPreferenceListener(preferenceListener: PreferenceListener) {
        listeners.add(preferenceListener)
    }

    override fun removePreferenceListener(preferenceListener: PreferenceListener) {
        listeners.remove(preferenceListener)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        Preference::class.sealedSubclasses.firstOrNull {
            it.objectInstance?.key == key
        }.let {
            listeners.forEach { listener ->
                listener.onPreferenceChanged(it!!.objectInstance!!)
            }
        }
    }

    companion object {
        private const val DEFAULT_PREFERENCE = "app"
    }
}