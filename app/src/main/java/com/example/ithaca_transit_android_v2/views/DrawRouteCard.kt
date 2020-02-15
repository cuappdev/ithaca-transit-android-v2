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

    val dotColor = Paint(Paint.ANTI_ALIAS_FLAG)
    var circleX1 = 20f //36
    var circleY1 = 20f //42
    var circleR = 20f


    var circleX2 = 20f //36
    var circleY2 = 220f //222



    var left = 15f //30
    var top = 30f //36
    var lineW =10f //10
    var lineL = 180f //180

    init {
        // create the Paint and set its color
        dotColor.setARGB(255, 0, 173, 255)


    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(circleX1, circleY1, circleR, dotColor)

        canvas.drawRect(left, top, left+lineW, top + lineL, dotColor)

        canvas.drawCircle(circleX2, circleY2, circleR, dotColor)
    }

}