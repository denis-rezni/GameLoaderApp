package com.example.gameloader

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

class OwnDrawAnimationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val rectSize = dp(56f)
    private val rectHypot = hypot(rectSize, rectSize)
    private val rectCornerRadius = dp(12f)
    private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFe1e3e6.toInt()

    }

    private val circleRadius = dp(28f)
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFe1e3e6.toInt()
    }

    private val margin = dp(14f)

    private val desiredWidth = (rectHypot + 2 * circleRadius * 1.5f + margin)
    private val desiredHeight = max(rectHypot, 2 * circleRadius * 1.5f)

    private val rectF = RectF()

    private var rectRotation: Float = 0f
        set(value) {
            field = value
            invalidate()
        }


    private var circleScale: Float = 1f
        set(value) {
            field = value
            invalidate()
        }

    private val rectRotateAnimator = ValueAnimator.ofFloat(0.0F, 180F).apply {
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener { rectRotation = it.animatedValue as Float }
    }
    private val circleScaleAnimator = ValueAnimator.ofFloat(1f, 1.5f, 1f).apply {
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener { circleScale = it.animatedValue as Float }
    }

    private var animator: Animator? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        animator?.cancel()
        animator = AnimatorSet().apply {
            interpolator = LinearInterpolator()
            playTogether(rectRotateAnimator, circleScaleAnimator)
            duration = 1000L
            start()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            getSize(widthMeasureSpec, desiredWidth.toInt()),
            getSize(heightMeasureSpec, desiredHeight.toInt())
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var save = canvas.save()

        val cx = rectHypot / 2
        val cy = rectHypot / 2

        canvas.rotate(rectRotation, cx, cy)

        val l = cx - rectSize / 2
        val t = cy - rectSize / 2

        rectF.set(l, t, l + rectSize, t + rectSize)
        canvas.drawRoundRect(rectF, rectCornerRadius, rectCornerRadius, rectPaint)

        canvas.restoreToCount(save)

        save = canvas.save()
        canvas.translate(rectHypot + margin, (desiredHeight / 2 - circleRadius))
//        canvas.scale(circleScale, circleScale, circleRadius, circleRadius)
        canvas.drawCircle(circleRadius, circleRadius, circleRadius, circlePaint)

        canvas.restoreToCount(save)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        animator?.cancel()
        animator = null
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