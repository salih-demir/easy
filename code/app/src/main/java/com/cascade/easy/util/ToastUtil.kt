package com.cascade.easy.util

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.doOnLayout
import com.cascade.easy.R

object ToastUtil {
    private const val BITMAP_SCALE = 0.15f
    private const val BLUR_RADIUS = 2f
    private const val CORNER_RADIUS = 200f

    @SuppressLint("InflateParams")
    fun buildToast(activity: Activity, message: CharSequence, blur: Float = BLUR_RADIUS): Toast {
        val viewToast = LayoutInflater.from(activity).inflate(R.layout.content_toast, null)
        val viewWindow = activity.window.decorView
        val bitmapWindow = BitmapUtil.getViewVisual(viewWindow)

        val backgroundColor = ContextCompat.getColor(activity, R.color.colorToastBackground)
        val gradientDrawable = GradientDrawable()
        gradientDrawable.setColor(backgroundColor)
        gradientDrawable.cornerRadius = CORNER_RADIUS

        viewToast.background = gradientDrawable
        viewToast.doOnLayout {
            val location = IntArray(2)
            viewToast.getLocationOnScreen(location)

            val x = location[0]
            val y = location[1]
            val width = viewToast.width
            val height = viewToast.height

            val croppedBitmap = BitmapUtil.cropBitmap(bitmapWindow, x, y, width, height)
            val blurredBitmap = BitmapUtil.blurBitmap(activity, croppedBitmap, BITMAP_SCALE, blur)

            val roundedDrawable = RoundedBitmapDrawableFactory
                .create(activity.resources, blurredBitmap).apply {
                    cornerRadius = CORNER_RADIUS
                }

            val drawableLayers = arrayOf(roundedDrawable, gradientDrawable)
            val layerDrawable = LayerDrawable(drawableLayers)

            viewToast.background = layerDrawable
        }

        val textViewToast = viewToast.findViewById<TextView>(R.id.textViewToast)
        textViewToast.text = message

        return Toast(activity).apply {
            view = viewToast
        }
    }
}