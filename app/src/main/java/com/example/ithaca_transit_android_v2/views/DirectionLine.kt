package com.example.ithaca_transit_android_v2.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class DirectionLine(context: Context) : View(context) {

    lateinit var primaryColor:Paint
    private var length:Float = 100f
    private var width: Float = 3f

    constructor(context: Context, colorStr: String, length: Float, width: Float):this(context) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        when (colorStr) {
            "blue" -> paint.setARGB(255, 7, 157, 220)
            "gray"-> paint.setARGB(255, 158, 158, 158)
        }
        this.primaryColor = paint
        this.length = length
        this.width = width
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRect(0f, -10f, width, length, primaryColor)
    }
}