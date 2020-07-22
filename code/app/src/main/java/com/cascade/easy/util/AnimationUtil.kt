package com.cascade.easy.util

import android.animation.Animator

abstract class SimpleAnimationListener : Animator.AnimatorListener {
    override fun onAnimationRepeat(animation: Animator) {}
    override fun onAnimationEnd(animation: Animator) {}
    override fun onAnimationCancel(animation: Animator) {}
    override fun onAnimationStart(animation: Animator) {}
}