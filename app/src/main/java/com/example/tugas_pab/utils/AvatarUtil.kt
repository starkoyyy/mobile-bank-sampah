package com.example.tugas_pab.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape

object AvatarUtil {
    private val colors = arrayOf(
        "#E57373", "#F06292", "#BA68C8", "#9575CD", "#7986CB", "#64B5F6",
        "#4FC3F7", "#4DD0E1", "#4DB6AC", "#81C784", "#AED581", "#FF8A65",
        "#D4E157", "#FFD54F", "#FFB74D", "#A1887F", "#90A4AE"
    )

    fun getAvatar(name: String): ShapeDrawable {
        val initial = if (name.isNotBlank()) name.trim().substring(0, 1).uppercase() else "?"
        
        // Pick a consistent color based on the name string hash
        val colorIndex = Math.abs(name.hashCode()) % colors.size
        val color = Color.parseColor(colors[colorIndex])

        return object : ShapeDrawable(OvalShape()) {
            override fun draw(canvas: Canvas) {
                super.draw(canvas)
                this.paint.color = color
                this.paint.style = Paint.Style.FILL
                
                val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    this.color = Color.WHITE
                    this.textSize = bounds.height() * 0.5f
                    this.textAlign = Paint.Align.CENTER
                }
                
                val textBounds = Rect()
                textPaint.getTextBounds(initial, 0, 1, textBounds)
                
                val xPos = bounds.width() / 2f
                val yPos = (bounds.height() / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)
                
                canvas.drawText(initial, xPos, yPos, textPaint)
            }
        }
    }
}
