package com.cascade.easy.activity

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import com.cascade.easy.R
import com.cascade.easy.activity.base.BaseActivity
import com.cascade.easy.util.SimpleAnimationListener
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        with(lottieAnimationView) {
            setFailureListener {
                startMainActivity()
            }
            addAnimatorListener(object : SimpleAnimationListener() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)

                    startMainActivity()
                }
            })
        }
    }

    fun startMainActivity() {
        val intentMainActivity = Intent(this, MainActivity::class.java)
        startActivity(intentMainActivity)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}