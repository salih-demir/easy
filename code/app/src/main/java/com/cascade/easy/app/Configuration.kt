package com.cascade.easy.app

import com.cascade.easy.data.NightMode

object Configuration {
    const val DEFAULT_LANGUAGE = Constants.LANG_CODE_ENGLISH

    val DEFAULT_DARK_MODE = NightMode.FOLLOW_SYSTEM
    val SUPPORTED_LANGUAGES = arrayOf(Constants.LANG_CODE_ENGLISH, Constants.LANG_CODE_TURKISH)
}