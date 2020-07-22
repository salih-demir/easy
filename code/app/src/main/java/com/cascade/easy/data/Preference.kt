package com.cascade.easy.data

import com.cascade.easy.model.preference.Settings
import kotlin.reflect.KClass

sealed class Preference<T : Any>(val key: String, val mClass: KClass<T>)
object SettingsPreference : Preference<Settings>("settings", Settings::class)