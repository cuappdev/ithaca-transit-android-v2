package com.example.ithaca_transit_android_v2.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class DirectionDot(context: Context) : View(context) {

    lateinit var primaryColor:Paint
    lateinit var whiteColor: Paint
    private var radius:Float = 20f
    private var useNestedCircles = false

    constructor(context: Context, colorStr: String, useNestedCircles: Boolean, radius: Float):this(context) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        when (colorStr) {
            "blue" -> paint.setARGB(255, 7, 157, 220)
            "gray"-> paint.setARGB(255, 158, 158, 158)
        }
        this.primaryColor = paint
        this.radius = radius
        this.useNestedCircles = useNestedCircles

        initWhitePaint()
    }

    private fun initWhitePaint() {
        val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        whitePaint.setARGB(255, 255, 255, 255)
        this.whiteColor = whitePaint
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(radius,radius, radius, primaryColor)
        if (useNestedCircles) {
            canvas.drawCircle(radius, radius, radius*2/3, this.whiteColor)
            canvas.drawCircle(radius, radius, radius/3, this.primaryColor)
        }
    }
}