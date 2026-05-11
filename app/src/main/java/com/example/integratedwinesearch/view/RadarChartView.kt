package com.example.integratedwinesearch.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class RadarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val labels = listOf("당도", "산미", "타닌", "바디", "알코올")
    private val values = listOf(3f, 6f, 7f, 8f, 5f)
    private val maxValue = 10f

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#D8DCE4")
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#C9CED8")
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
    }

    private val valueFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#6632A060")
        style = Paint.Style.FILL
    }

    private val valueStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2A8E58")
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#7E8594")
        textSize = 34f
        textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f + 8f
        val radius = min(width, height) * 0.33f
        val count = labels.size

        for (level in 1..4) {
            val r = radius * (level / 4f)
            canvas.drawPath(polygonPath(cx, cy, r, count), gridPaint)
        }

        for (i in 0 until count) {
            val angle = angleFor(i)
            val x = cx + radius * cos(angle)
            val y = cy + radius * sin(angle)
            canvas.drawLine(cx, cy, x, y, axisPaint)
        }

        val valuePath = Path()
        for (i in 0 until count) {
            val angle = angleFor(i)
            val ratio = values[i] / maxValue
            val x = cx + radius * ratio * cos(angle)
            val y = cy + radius * ratio * sin(angle)
            if (i == 0) valuePath.moveTo(x, y) else valuePath.lineTo(x, y)
        }
        valuePath.close()
        canvas.drawPath(valuePath, valueFillPaint)
        canvas.drawPath(valuePath, valueStrokePaint)

        for (i in 0 until count) {
            val angle = angleFor(i)
            val x = cx + (radius + 34f) * cos(angle)
            val y = cy + (radius + 34f) * sin(angle) + 10f
            canvas.drawText(labels[i], x, y, labelPaint)
        }
    }

    private fun polygonPath(cx: Float, cy: Float, r: Float, count: Int): Path {
        val path = Path()
        for (i in 0 until count) {
            val angle = angleFor(i)
            val x = cx + r * cos(angle)
            val y = cy + r * sin(angle)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        return path
    }

    private fun angleFor(index: Int): Float {
        val slice = (2 * PI / labels.size).toFloat()
        return -PI.toFloat() / 2f + slice * index
    }
}

