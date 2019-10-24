package com.example.gameloader

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.PathInterpolator
import androidx.core.animation.doOnRepeat
import kotlin.math.min

class LoaderDrawAnimationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val circleRadius = dp(2.5f)
    private val dotSide = dp(6f)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFe1e3e6.toInt()
    }
    private val betweenCircles = dp(10f)

    private val rectWidth = dp(22f)
    private val rectHeight = dp(6f)


    private val margin = dp(16f)
    private val miniMargin = dp(2f)
    private val desiredWidth = 2 * miniMargin + margin + rectWidth + dotSide * 2 + betweenCircles
    private val desiredHeight = (rectWidth + 2 * miniMargin)
    private var turnCounter = 0

    private var circleScale: Float = 1f
        set(value) {
            field = value
            invalidate()
        }

    private var rectRotation: Float = 0f
        set(value) {
            field = value
            invalidate()
        }


    private val circleScaleAnimator = ValueAnimator.ofFloat(1f, 1.3f, 1f).apply {
        repeatCount = ValueAnimator.INFINITE
        this.doOnRepeat {
            turnCounter++
            turnCounter %= 4
        }
        addUpdateListener {
            circleScale = it.animatedValue as Float
        }
    }

    private val rectRotateAnimator = ValueAnimator.ofFloat(0.0F, 360F).apply {
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener { rectRotation = it.animatedValue as Float }
    }

    private var animator: Animator? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        animator?.cancel()
        animator = AnimatorSet().apply {
            interpolator = PathInterpolator(0.25F, 0.1F, 0.25F, 1F)
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

    private val tillCircles = miniMargin + margin + rectWidth

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var save = canvas.save()
        canvas.translate(tillCircles, miniMargin + (dotSide + betweenCircles) / 2)
        if (turnCounter == 3) {
            canvas.scale(circleScale, circleScale, circleRadius, circleRadius)
        }
        canvas.drawRoundRect(0f, 0f, dotSide, dotSide, circleRadius, circleRadius, paint)

        canvas.restoreToCount(save)
        save = canvas.save()

        canvas.translate(tillCircles + (betweenCircles + dotSide) / 2, miniMargin)
        if (turnCounter == 0) {
            canvas.scale(circleScale, circleScale, circleRadius, circleRadius)
        }
        canvas.drawRoundRect(0f, 0f, dotSide, dotSide, circleRadius, circleRadius, paint)

        canvas.restoreToCount(save)
        save = canvas.save()

        canvas.translate(
            tillCircles + (betweenCircles + dotSide) / 2,
            miniMargin + dotSide + betweenCircles
        )
        if (turnCounter == 2) {
            canvas.scale(circleScale, circleScale, circleRadius, circleRadius)
        }
        canvas.drawRoundRect(0f, 0f, dotSide, dotSide, circleRadius, circleRadius, paint)

        canvas.restoreToCount(save)
        save = canvas.save()

        canvas.translate(
            tillCircles + dotSide + betweenCircles, miniMargin + (dotSide + betweenCircles) / 2
        )
        if (turnCounter == 1) {
            canvas.scale(circleScale, circleScale, circleRadius, circleRadius)
        }
        canvas.drawRoundRect(0f, 0f, dotSide, dotSide, circleRadius, circleRadius, paint)

        canvas.restoreToCount(save)
        save = canvas.save()

        val bigMargin = miniMargin + (rectWidth - rectHeight) / 2
        //horisontal roundRect
        canvas.rotate(rectRotation, miniMargin + rectWidth / 2, bigMargin + rectHeight / 2)
        canvas.drawRoundRect(
            miniMargin, bigMargin, miniMargin + rectWidth,
            bigMargin + rectHeight, circleRadius, circleRadius, paint
        )

        canvas.restoreToCount(save)
        save = canvas.save()

        //vertical roundRect
        canvas.rotate(rectRotation, bigMargin + rectHeight / 2, miniMargin + rectWidth / 2)
        canvas.drawRoundRect(
            bigMargin, miniMargin, bigMargin + rectHeight,
            miniMargin + rectWidth, circleRadius, circleRadius, paint
        )
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