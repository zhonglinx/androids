package com.example.android.ui.compass

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.withRotation
import androidx.core.graphics.withSave
import com.example.android.R
import com.example.android.util.getColor
import java.lang.Math.toDegrees
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.sin

class CompassView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyle: Int = 0
) : View(context, attrs, defStyle) {

  private val labels = listOf("N", "E", "S", "W")

  private val degreeLabels = listOf("30", "60", "120", "150", "210", "240", "300", "330")

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

  private val shaderPaint = Paint(Paint.ANTI_ALIAS_FLAG)

  private val outerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = getColor(R.color.compass_outer_color)
    strokeWidth = 4f
  }

  private val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    strokeWidth = 4f
  }

  private val scalePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = getColor(R.color.compass_inner_color)
    strokeWidth = 3f
  }

  private val textBound = Rect()

  private val labelPaint: Paint

  private val degreePaint: Paint

  private val textPaint: Paint

  var degree: Float = 0f
    set(value) {
      field = value
      invalidate()
    }

  init {
    val density = resources.displayMetrics.scaledDensity
    labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      textSize = density * LABEL_TEXT_SIZE
    }

    degreePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      textSize = density * DEGREE_TEXT_SIZE
      color = getColor(R.color.compass_inner_color)
    }

    textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      textSize = density * OFFSET_DEGREE_TEXT_SIZE
      color = getColor(R.color.white)
    }
  }

  override fun onSizeChanged(
    w: Int,
    h: Int,
    oldw: Int,
    oldh: Int
  ) {
    super.onSizeChanged(w, h, oldw, oldh)

    // 外层半径为宽高最小值的二分之一
    outerRadius = w.coerceAtMost(h) * 3 / 4f / 2
    innerRadius = outerRadius * 3 / 4f
    gradientRadius = outerRadius * 9 / 10f
    arrowWidth = outerRadius / 8f
    scaleHeight = outerRadius / 14f

    centerX = w / 2f
    centerY = h / 2f

    val shader = RadialGradient(
      centerX,
      centerY,
      gradientRadius,
      intArrayOf(
        getColor(R.color.compass_outer_color),
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
    canvas.withRotation(degrees = degree, centerX, centerY) {
      drawInner(canvas)
    }
    // 绘制偏移的度数
    val offsetDegree = "${abs(ceil(degree).toInt())}°"
    textPaint.getTextBounds(offsetDegree, 0, offsetDegree.length, textBound)
    val startX = centerX - textBound.width() / 2f
    val baseline = centerY + textBound.height() / 2f
    canvas.drawText(offsetDegree, startX, baseline, textPaint)
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
    makeArrowPathByRadius(outerRadius)
    outerPaint.style = Paint.Style.FILL_AND_STROKE
    canvas.drawPath(arrowPath, outerPaint)
  }

  private fun drawInner(canvas: Canvas) {
    innerPaint.apply {
      color = getColor(R.color.compass_inner_color)
      style = Paint.Style.STROKE
    }
    canvas.drawCircle(centerX, centerY, innerRadius, innerPaint)

    // 绘制指南针
    makeArrowPathByRadius(innerRadius)

    innerPaint.apply {
      color = getColor(R.color.compass_indicator_color)
      style = Paint.Style.FILL_AND_STROKE
    }
    canvas.drawPath(arrowPath, innerPaint)

    // 绘制刻度
    val offset = centerY - innerRadius + 14

    canvas.withSave {
      val x = centerX - scalePaint.strokeWidth / 2f
      repeat(240) { index ->
        if (index % 60 == 0) {
          scalePaint.color = getColor(R.color.compass_inner_color)
        } else {
          scalePaint.color = getColor(R.color.compass_outer_color)
        }
        drawLine(x, offset, x, offset + scaleHeight, scalePaint)

        rotate(1.5f, centerX, centerY)
      }
    }

    // 绘制方位
    drawPosition(canvas, offset = offset + scaleHeight)
    // 绘制度数
    drawDegree(canvas, offset + scaleHeight)
  }

  private fun drawPosition(canvas: Canvas, offset: Float) {
    canvas.withSave {
      labels.forEachIndexed { index, label ->
        if (index == 0) {
          labelPaint.color = getColor(R.color.compass_indicator_color)
        } else {
          labelPaint.color = getColor(R.color.white)
        }
        labelPaint.getTextBounds(label, 0, label.length, textBound)

        val startX = centerX - textBound.width() / 2f
        val baseline = offset + textBound.height() + 10

        canvas.drawText(label, startX, baseline, labelPaint)

        canvas.rotate(90f, centerX, centerY)
      }
    }
  }

  private fun drawDegree(canvas: Canvas, offset: Float) {
    canvas.withSave {
      degreeLabels.forEachIndexed { index, degree ->
        degreePaint.getTextBounds(degree, 0, degree.length, textBound)
        val startX = centerX - textBound.width() / 2f
        val baseline = offset + 10 + textBound.height()
        canvas.rotate(if (index % 2 == 0 && index != 0) 60f else 30f, centerX, centerY)
        canvas.drawText(degree, startX, baseline, degreePaint)
      }
    }
  }

  private fun makeArrowPathByRadius(radius: Float) {
    arrowPath.run {
      reset()
      moveTo(centerX - arrowWidth / 2f, centerY - radius)
      lineTo(centerX + arrowWidth / 2f, centerY - radius)
      lineTo(centerX, centerY - radius - arrowWidth * sin(toDegrees(60.0)).toFloat())
      close()
    }
  }

  companion object {
    private const val LABEL_TEXT_SIZE = 13
    private const val DEGREE_TEXT_SIZE = 8
    private const val OFFSET_DEGREE_TEXT_SIZE = 40
  }
}