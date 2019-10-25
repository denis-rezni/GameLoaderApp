package com.example.gameloader

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.PathInterpolator
import androidx.core.animation.doOnEnd
import kotlin.math.min

class LoaderDrawAnimationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val circleRadius = dp(2.5f)
    private val dotSide = dp(6f)
    private val dotRadius = dotSide / 2
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


    private var turn = 0
    private val pv1 = PropertyValuesHolder.ofFloat("rectRotation", 0f, 180f)
    private val pv2 = PropertyValuesHolder.ofFloat("circleScale", 1f, 1.3f)
    private val wholeAnimationStep = ValueAnimator.ofPropertyValuesHolder(pv1, pv2).apply {
        addUpdateListener {
            rectRotation = it.getAnimatedValue("rectRotation") as Float
            circleScale = it.getAnimatedValue("circleScale") as Float
        }
        doOnEnd {
            turn++
            turn %= 6
            if(turn == 0){
                postDelayed({ this.start() }, 1000L)
            } else {
                this.start()
            }
        }
    }

    private var animator: Animator? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        animator?.cancel()
        animator = AnimatorSet().apply {
            interpolator = PathInterpolator(0.25F, 0.1F, 0.25F, 1F)
            play(wholeAnimationStep)
            duration = 300L
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

        canvas.translate(tillCircles + (betweenCircles + dotSide) / 2, miniMargin)
        if (turn == 1) {
            canvas.scale(circleScale, circleScale, dotRadius, dotRadius)
        } else if (turn == 2) {
            canvas.scale(2.3f - circleScale, 2.3f - circleScale, dotRadius, dotRadius)
        }
        canvas.drawRoundRect(0f, 0f, dotSide, dotSide, circleRadius, circleRadius, paint)

        canvas.restoreToCount(save)
        save = canvas.save()

        canvas.translate(
            tillCircles + dotSide + betweenCircles, miniMargin + (dotSide + betweenCircles) / 2
        )
        if (turn == 2) {
            canvas.scale(circleScale, circleScale, dotRadius, dotRadius)
        } else if (turn == 3) {
            canvas.scale(2.3f - circleScale, 2.3f - circleScale, dotRadius, dotRadius)
        }
        canvas.drawRoundRect(0f, 0f, dotSide, dotSide, circleRadius, circleRadius, paint)

        canvas.restoreToCount(save)
        save = canvas.save()

        canvas.translate(
            tillCircles + (betweenCircles + dotSide) / 2,
            miniMargin + dotSide + betweenCircles
        )
        if (turn == 3) {
            canvas.scale(circleScale, circleScale, dotRadius, dotRadius)
        } else if (turn == 4) {
            canvas.scale(2.3f - circleScale, 2.3f - circleScale, dotRadius, dotRadius)
        }
        canvas.drawRoundRect(0f, 0f, dotSide, dotSide, circleRadius, circleRadius, paint)

        canvas.restoreToCount(save)
        save = canvas.save()

        canvas.translate(tillCircles, miniMargin + (dotSide + betweenCircles) / 2)
        if (turn == 4) {
            canvas.scale(circleScale, circleScale, dotRadius, dotRadius)
        } else if (turn == 5) {
            canvas.scale(2.3f - circleScale, 2.3f - circleScale, dotRadius, dotRadius)
        }
        canvas.drawRoundRect(0f, 0f, dotSide, dotSide, circleRadius, circleRadius, paint)

        canvas.restoreToCount(save)
        save = canvas.save()

        val bigMargin = miniMargin + (rectWidth - rectHeight) / 2
        //horisontal roundRect
        if (turn == 0) {
            canvas.rotate(rectRotation, miniMargin + rectWidth / 2, bigMargin + rectHeight / 2)
        }
        canvas.drawRoundRect(
            miniMargin, bigMargin, miniMargin + rectWidth,
            bigMargin + rectHeight, circleRadius, circleRadius, paint
        )

        canvas.restoreToCount(save)
        save = canvas.save()

        //vertical roundRect
        if (turn == 0) {
            canvas.rotate(rectRotation, bigMargin + rectHeight / 2, miniMargin + rectWidth / 2)
        }
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