package com.cascade.easy.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.cascade.easy.R
import com.cascade.easy.activity.base.BaseActivity
import com.cascade.easy.adapter.MainContentAdapter
import com.cascade.easy.data.MainContent
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_navigation_tabs.*

class MainActivity : BaseActivity(), Observer<MainContent> {
    private val liveDataMainContent = MutableLiveData<MainContent>().apply {
        observe(this@MainActivity, this@MainActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Initialize ActionBar.
        supportActionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setCustomView(R.layout.content_navigation_tabs)

            val isTablet = resources.getBoolean(R.bool.isTablet)
            it.setDisplayShowTitleEnabled(isTablet)
        }

        // Initialize ViewPager.
        with(viewPagerMainContent) {
            adapter = MainContentAdapter(supportFragmentManager, lifecycle)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    MainContent.values()[position].let {
                        liveDataMainContent.postValue(it)
                    }
                }
            })
        }

        // Initialize TabLayout.
        TabLayoutMediator(tabLayoutNavigation, viewPagerMainContent, true,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                MainContent.values()[position].let {
                    tab.text = getText(it.titleRes)
                }
            }).attach()

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        handleIntent(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        MainContent.values().forEach {
            menu.setGroupVisible(it.menuGroupId, false)
        }
        liveDataMainContent.value?.let {
            menu.setGroupVisible(it.menuGroupId, true)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val messageResId = when (item.itemId) {
            R.id.itemHelp -> R.string.message_help
            R.id.itemAbout -> R.string.message_about
            else -> null
        }

        messageResId?.let {
            AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(it)
                .setPositiveButton(R.string.label_ok) { dialog, _ -> dialog.dismiss() }
                .show()

            return true
        } ?: run {
            return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (viewPagerMainContent.currentItem != 0) {
            viewPagerMainContent.setCurrentItem(0, true)
        } else {
            super.onBackPressed()
        }
    }

    override fun onChanged(t: MainContent?) {
        invalidateOptionsMenu()
    }

    private fun handleIntent(intent: Intent) {
        // Select target content.
        intent.extras?.getString(KEY_TARGET_MAIN_CONTENT)?.let {
            MainContent.valueOf(it)
        }.let {
            MainContent.values().indexOf(it).let { index ->
                viewPagerMainContent.setCurrentItem(index, false)
            }
        }
    }

    companion object {
        const val KEY_TARGET_MAIN_CONTENT = "target_content"

        fun createOptions(mainContent: MainContent) =
            Bundle().apply {
                putString(KEY_TARGET_MAIN_CONTENT, mainContent.name)
            }
    }
}