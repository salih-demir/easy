package com.cascade.easy.fragment.base

import androidx.fragment.app.Fragment
import com.cascade.easy.activity.base.BaseActivity
import com.cascade.easy.app.AppModule

abstract class BaseFragment : Fragment(), AppModule {
    override val preferenceService by lazy { baseActivity.preferenceService }
    override val networkService by lazy { baseActivity.networkService }
    override val speechService by lazy { baseActivity.speechService }

    private val baseActivity by lazy { activity as BaseActivity }

    override fun provideContext() = requireContext()
}