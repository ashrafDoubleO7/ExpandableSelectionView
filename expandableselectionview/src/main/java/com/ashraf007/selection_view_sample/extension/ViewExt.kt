package com.ashraf007.selection_view_sample.extension

import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.core.view.isGone



fun View.expand(
    maxExpandedHeight: Int,
    animationDurationScale: Int,
    completionListener: (() -> Unit)? = null
) {
    measure(MATCH_PARENT, WRAP_CONTENT)
    val targetHeight = Math.min(measuredHeight, maxExpandedHeight)

    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
    layoutParams.height = 1
    visibility = View.VISIBLE
    val a = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            layoutParams.height = when (interpolatedTime) {
                1f -> {
                    completionListener?.invoke()
                    WRAP_CONTENT
                }
                else -> (targetHeight * interpolatedTime).toInt()
            }
            requestLayout()
        }

        override fun willChangeBounds() = true
    }
    // 1dp/ms
    a.duration = ((targetHeight / context.screenDensity) * animationDurationScale).toLong()
    startAnimation(a)
}

fun View.collapse(
    animationDurationScale: Int,
    completionListener: (() -> Unit)? = null
) {
    val initialHeight = measuredHeight

    val a = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            if (interpolatedTime == 1f) {
                isGone = true
                completionListener?.invoke()
            } else {
                layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                requestLayout()
            }
        }

        override fun willChangeBounds() = true
    }
    // 1dp/ms
    a.duration = ((initialHeight / context.screenDensity) * animationDurationScale).toLong()
    startAnimation(a)
}