package com.example.gameloader

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.min


class AnimatedTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        //        color = 0xFFc2e8ce.toInt()
        color = Color.BLACK
    }
    private val desiredWidth = dp(64f)
    private val desiredHeight = dp(40f)
    private val text = resources.getString(R.string.loading)

    private var textAlpha: Int = 255
        set(value) {
            field = value
            invalidate()
        }

    private val textAlphaAnimator = ValueAnimator.ofInt(255, 100, 255).apply {
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener {
            textAlpha = it.animatedValue as Int
        }
    }

    private var animator: Animator? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        animator?.cancel()
        animator = AnimatorSet().apply {
            interpolator = LinearInterpolator()
            playTogether(textAlphaAnimator)
            duration = 1000L
            start()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        correctTextSize()
        setMeasuredDimension(
            getSize(widthMeasureSpec, desiredWidth.toInt()),
            getSize(heightMeasureSpec, desiredHeight.toInt())
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val save = canvas.save()
//        paint.textSize = dp(18f)
        paint.alpha = textAlpha
        canvas.drawText(text, 0f, desiredHeight * 0.75f, paint)
        canvas.restoreToCount(save)
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        animator?.cancel()
        animator = null
    }

    private fun correctTextSize() {
        var low = 0f
        var high = 100f
        var m: Float
        for (i in 0 until 10) {
            m = (low + high) / 2
            paint.textSize = m;
            if (paint.measureText(text) > desiredWidth) {
                high = m
            } else {
                low = m
            }
        }
    }


    private fun getSize(measureSpec: Int, desired: Int): Int {
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        return when (mode) {
            MeasureSpec.AT_MOST -> min(size, desired)
            MeasureSpec.EXACTLY -> size
            MeasureSpec.UNSPECIFIED -> desired
            else -> desired
        }
    }

    private fun dp(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }
}