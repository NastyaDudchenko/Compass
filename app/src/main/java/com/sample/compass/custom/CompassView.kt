package com.sample.compass.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.sample.compass.R

class CompassView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var baseColor = ContextCompat.getColor(context, R.color.primaryDarkColor)
    private var arrowColor = ContextCompat.getColor(context, R.color.secondaryColor)
    private var compassBaseWidth = DEFAULT_COMPASS_BASE_WIDTH

    init {
        context.obtainStyledAttributes(attrs, R.styleable.CompassView, 0, 0).apply {
            try {
                baseColor = getColor(R.styleable.CompassView_baseColor, baseColor)
                compassBaseWidth = getFloat(
                    R.styleable.CompassView_compassBaseWidth,
                    compassBaseWidth
                )
            } finally {
                recycle()
            }
        }
        setPadding(PADDING, PADDING, PADDING, PADDING)
    }

    private var compassBasePaint = setPaint(baseColor, compassBaseWidth)
    private var arcPaint = setPaint(arrowColor, compassBaseWidth)
    private var trianglePaint = Paint().apply {
        color = arrowColor
        isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        drawCompassBase(canvas)
        drawArrow(canvas)
    }

    private fun setPaint(paintColor: Int, paintWidth: Float): Paint {
        return Paint().apply {
            color = paintColor
            style = Paint.Style.STROKE
            strokeWidth = paintWidth
            isAntiAlias = true
        }
    }

    private fun drawArrow(canvas: Canvas) {
        drawArc(canvas)
        drawTriangle(canvas)
    }

    private fun drawCompassBase(canvas: Canvas) {
        val radius = measuredWidth / 2F
        canvas.drawCircle(radius, radius, radius - PADDING, compassBasePaint)
    }

    private fun drawArc(canvas: Canvas) {
        val oval = RectF(
            0F + PADDING, 0F + PADDING,
            (measuredWidth - PADDING).toFloat(),
            (measuredHeight - PADDING).toFloat()
        )
        canvas.drawArc(oval, START_ANGLE, SWEEP_ANGLE, false, arcPaint)
    }

    private fun drawTriangle(canvas: Canvas) {
        val offsetX = measuredWidth / 2f
        val offsetY = 0F
        val halfTriangleWidth = TRIANGLE_WIDTH / 2

        val path = Path()
        with(path) {
            moveTo(offsetX, offsetY)
            lineTo(offsetX - halfTriangleWidth, offsetY + halfTriangleWidth)
            lineTo(offsetX + halfTriangleWidth, offsetY + halfTriangleWidth)
            lineTo(offsetX, offsetY)
            close()
        }

        canvas.drawPath(path, trianglePaint)
    }

    companion object {
        private const val PADDING = 64
        private const val DEFAULT_COMPASS_BASE_WIDTH = 30F
        private const val TRIANGLE_WIDTH = 150F

        private const val INCLINE_ANGLE = 40F
        private const val START_ANGLE = -90F - INCLINE_ANGLE
        private const val SWEEP_ANGLE = INCLINE_ANGLE * 2
    }
}