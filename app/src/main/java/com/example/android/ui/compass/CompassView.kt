package com.example.android.ui.compass

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.withSave
import com.example.android.R
import java.lang.Math.toDegrees
import kotlin.math.sin

class CompassView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    /** 外层半径 */
    private var outerRadius = 0f

    /** 内层半径 */
    private var innerRadius = 0f

    private var gradientRadius = 0f

    private var arrowWidth = 0f

    private var scaleHeight = 5f

    private var centerX = 0f
    private var centerY = 0f

    private val arrowPath = Path()

    private lateinit var shader: Shader

    private val shaderPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val outerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(getContext(), R.color.compass_outer_color)
        strokeWidth = 4f
    }

    private val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 4f
    }

    private val scalePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(getContext(), R.color.compass_inner_color)
        strokeWidth = 3f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // 外层半径为宽高最小值的二分之一
        outerRadius = w.coerceAtMost(h) * 3 / 4f / 2
        innerRadius = outerRadius * 3 / 4f
        gradientRadius = outerRadius * 9 / 10f
        arrowWidth = outerRadius / 8f
        scaleHeight = outerRadius / 14

        centerX = w / 2f
        centerY = h / 2f

        shader = RadialGradient(
            centerX,
            centerY,
            gradientRadius,
            intArrayOf(
                ContextCompat.getColor(context, R.color.compass_outer_color),
                Color.TRANSPARENT
            ),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        shaderPaint.shader = shader
    }

    override fun onDraw(canvas: Canvas) {
        // 绘制中间渐变
        drawGradient(canvas)
        // 绘制外层
        drawOuter(canvas)
        // 绘制内层
        drawInner(canvas)
    }

    private fun drawGradient(canvas: Canvas) {
        canvas.drawCircle(centerX, centerY, gradientRadius, shaderPaint)
    }

    private fun drawOuter(canvas: Canvas) {
        outerPaint.style = Paint.Style.STROKE
        canvas.drawArc(
            centerX - outerRadius,
            centerY - outerRadius,
            centerX + outerRadius,
            centerY + outerRadius,
            -80f, 150f, false, outerPaint
        )

        canvas.drawArc(
            centerX - outerRadius,
            centerY - outerRadius,
            centerX + outerRadius,
            centerY + outerRadius,
            110f, 150f, false, outerPaint
        )

        // 绘制三角形
        outerPaint.style = Paint.Style.FILL_AND_STROKE
        arrowPath.run {
            reset()
            moveTo(centerX - arrowWidth / 2f, centerY - outerRadius)
            lineTo(centerX + arrowWidth / 2f, centerY - outerRadius)
            lineTo(centerX, centerY - outerRadius - arrowWidth * sin(toDegrees(60.0)).toFloat())
            close()
        }

        canvas.drawPath(arrowPath, outerPaint)
    }

    private fun drawInner(canvas: Canvas) {
        innerPaint.apply {
            color = ContextCompat.getColor(context, R.color.compass_inner_color)
            style = Paint.Style.STROKE
        }
        canvas.drawCircle(centerX, centerY, innerRadius, innerPaint)

        arrowPath.run {
            reset()
            moveTo(centerX - arrowWidth / 2f, centerY - innerRadius)
            lineTo(centerX + arrowWidth / 2f, centerY - innerRadius)
            lineTo(centerX, centerY - innerRadius - arrowWidth * sin(toDegrees(60.0)).toFloat())
            close()
        }

        innerPaint.apply {
            color = ContextCompat.getColor(context, R.color.compass_indicator_color)
            style = Paint.Style.FILL_AND_STROKE
        }
        canvas.drawPath(arrowPath, innerPaint)

        // 绘制刻度
        canvas.withSave {
            val x = centerX - scalePaint.strokeWidth / 2f
            val offset = centerY - innerRadius + 14
            repeat(360) {
                if (it % 90 == 0) {
                    scalePaint.color = ContextCompat.getColor(context, R.color.compass_inner_color)
                } else {
                    scalePaint.color = ContextCompat.getColor(context, R.color.compass_outer_color)
                }
                drawLine(x, offset, x, offset + scaleHeight, scalePaint)
                rotate(1f, centerX, centerY)
            }
        }
    }

}