package com.cascade.easy.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cascade.easy.data.MainContent
import com.cascade.easy.fragment.feedback.FeedbackListFragment
import com.cascade.easy.fragment.settings.SettingsFragment

class MainContentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return MainContent.values()[position].let {
            when (it) {
                MainContent.FEEDBACK_LIST -> FeedbackListFragment.newInstance()
                MainContent.SETTINGS -> SettingsFragment.newInstance()
            }
        }
    }

    override fun getItemCount() = MainContent.values().size
}