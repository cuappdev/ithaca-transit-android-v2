package com.example.ithaca_transit_android_v2.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View

class DirectionDot(context: Context) : View(context) {

    lateinit var primaryColor:Paint
    lateinit var whiteColor: Paint
    private var useNestedCircles = false
    private var drawSegmentAbove:Boolean = false
    private var drawSegmentBelow:Boolean = false
    private var radius:Float = -1f
    private var verticalPadding:Float = -1f
    private var lineWidth:Float = -1f

    constructor(context: Context, colorStr: String,
                useNestedCircles: Boolean,
                drawSegmentAbove:Boolean,
                drawSegmentBelow:Boolean,
                radius: Float,
                lineWidth: Float,
                verticalPadding:Float):this(context) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        when (colorStr) {
            "blue" -> paint.setARGB(255, 7, 157, 220)
            "gray"-> paint.setARGB(255, 158, 158, 158)
        }
        this.primaryColor = paint
        this.radius = radius
        this.useNestedCircles = useNestedCircles
        this.drawSegmentAbove = drawSegmentAbove
        this.drawSegmentBelow = drawSegmentBelow
        this.verticalPadding = verticalPadding
        this.lineWidth = lineWidth

        initWhitePaint()
    }

    private fun initWhitePaint() {
        val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        whitePaint.setARGB(255, 255, 255, 255)
        this.whiteColor = whitePaint
    }

    override fun onDraw(canvas: Canvas) {
        val leftMargin = (radius * 2 - lineWidth)/2
        if (drawSegmentAbove) {
            canvas.drawRect(leftMargin, 0f, leftMargin + lineWidth, radius, primaryColor)
        }
        if (drawSegmentBelow) {
            canvas.drawRect(leftMargin, radius + this.verticalPadding,
                leftMargin + lineWidth, 2*(radius+this.verticalPadding), primaryColor)
        }
        canvas.drawCircle(radius,radius + this.verticalPadding, radius, primaryColor)
        if (useNestedCircles) {
            canvas.drawCircle(radius, radius + this.verticalPadding, radius*2/3, this.whiteColor)
            canvas.drawCircle(radius, radius + this.verticalPadding, radius/3, this.primaryColor)
        }
    }
}