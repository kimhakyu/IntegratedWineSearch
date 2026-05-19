package com.example.integratedwinesearch.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class StorageLineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E8EBF0")
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1F8B4C")
        strokeWidth = 6f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1F8B4C")
        style = Paint.Style.FILL
    }

    private val points = floatArrayOf(16.0f, 15.8f, 16.2f, 16.5f, 16.3f, 16.1f, 16.0f)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val left = paddingLeft.toFloat()
        val top = paddingTop.toFloat()
        val right = width - paddingRight.toFloat()
        val bottom = height - paddingBottom.toFloat()
        if (right <= left || bottom <= top) return

        val chartHeight = bottom - top
        val chartWidth = right - left

        val minV = points.minOrNull() ?: 0f
        val maxV = points.maxOrNull() ?: 1f
        val range = max(0.5f, maxV - minV)

        // horizontal grid
        for (i in 0..3) {
            val y = top + chartHeight * i / 3f
            canvas.drawLine(left, y, right, y, gridPaint)
        }
        // vertical grid
        for (i in 0..6) {
            val x = left + chartWidth * i / 6f
            canvas.drawLine(x, top, x, bottom, gridPaint)
        }

        val path = Path()
        points.forEachIndexed { index, v ->
            val x = left + chartWidth * index / (points.size - 1).toFloat()
            val ratio = (v - minV) / range
            val y = bottom - ratio * chartHeight
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        canvas.drawPath(path, linePaint)

        points.forEachIndexed { index, v ->
            val x = left + chartWidth * index / (points.size - 1).toFloat()
            val ratio = (v - minV) / range
            val y = bottom - ratio * chartHeight
            canvas.drawCircle(x, y, 7f, dotPaint)
        }
    }
}
