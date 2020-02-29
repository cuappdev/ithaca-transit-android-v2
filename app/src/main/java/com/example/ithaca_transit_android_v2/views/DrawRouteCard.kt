package com.example.ithaca_transit_android_v2.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View

class DrawRouteCard(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    var blueDotColor = Paint(Paint.ANTI_ALIAS_FLAG)
    var grayDotColor = Paint(Paint.ANTI_ALIAS_FLAG)
    var whiteDotColor = Paint(Paint.ANTI_ALIAS_FLAG)

    //Dimensions for Blue Dots and Line
    var circleX1 = 20f //36
    var circleY1 = 20f //42
    var circleR = 20f

    var circleX2 = 36f //36
    var circleY2 = 222f //222

    var left = 15f //30
    var top = 30f //36
    var lineW = 10f //10
    var lineL = 300f //180

    var dotSpace = 30f

    //Dimensions for gray dots
    var circleX3 = 20f //36
    var circleY3 = 20f //42
    var circleR3 = 8f

    var circleX4 = 20f //36
    var circleY4 = 8f //42

    var circleX5 = 20f //36
    var circleY5 = 20f //42
    var circleR5 = 20f

    var circleX6 = 20f //36
    var circleY6 = 20f //42
    var circleR6 = 15f


    var circleX7 = 20f //36
    var circleY7 = 20f //42
    var circleR7 = 8f

    init {
        // create the Paint and set its color
        blueDotColor.setARGB(255, 0, 173, 255)
        grayDotColor.setARGB(255, 160, 160, 160)
        whiteDotColor.setARGB(255, 255, 255, 255)

    }

    fun setBlueDimensions(circlex2: Float, circley2: Float, lineL: Float) {
        circleX2 = circlex2
        circleY2 = circley2
        this.lineL = lineL

    }

    //fun setGrayDimensions()

    fun setGrayDimensions(x: Float, y3: Float, y5: Float, dotSpace: Float) {
        circleX3 = x
        circleY3 = y3

        circleX4 = x
        circleY4 = (circleY3 + dotSpace)

        circleX5 = x
        circleY5 = y5

        circleX6 = x
        circleY6 = y5

        circleX7 = x
        circleY7 = y5

    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(circleX1, circleY1, circleR, blueDotColor)

        canvas.drawRect(left, top, left + lineW, top + lineL, blueDotColor)

        canvas.drawCircle(circleX2, circleY2, circleR, blueDotColor)

        canvas.drawCircle(circleX3, circleY3, circleR3, grayDotColor)
        canvas.drawCircle(circleX4, circleY4, circleR3, grayDotColor)
        canvas.drawCircle(circleX5, circleY5, circleR5, grayDotColor)
        canvas.drawCircle(circleX6, circleY6, circleR6, whiteDotColor)
        canvas.drawCircle(circleX7, circleY7, circleR7, grayDotColor)

    }

}