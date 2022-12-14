package com.example.minipaint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat

// brush stroke : KEEPING CONSTANT
private const val STROKE_WIDTH = 20f


class MyCanvasView (context: Context): View(context){


    // These are the bitmap and canvas for caching/storing what has been drawn before
   private lateinit var extraCanvas: Canvas
   private lateinit var extraBitmap: Bitmap



   private val backgroundColor = ResourcesCompat.getColor(resources,R.color.colorBackground, null)
    private val drawColor = ResourcesCompat.getColor(resources,R.color.colorPaint,null)
    // holding the colour from which to draw


    private val paint = Paint().apply{
        color = drawColor
        isAntiAlias = true // smoothens out the edges of what is drawn without effecting the shape
        isDither = true // colours with highers precision are down sampled by dithering
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = STROKE_WIDTH
    }



    private var path = Path()  // to store the path that is being drawn when the users touch on the screen
    private var motionTouchEventX =0f  //variables for caching the x and y coordinates
    private  var motionTouchEventY = 0f // of current touch event



    private var currentX =0f
    private var currentY = 0f //these are the starting point for the next path (the next segment of the line to draw).


    private val touchTolerance= 50f

    private fun touchUp(){
        // Reset the path so it doesn't get drawn again.
        path.reset()
    }
    private  fun touchMove(){
       val dx = Math.abs(motionTouchEventX - currentX)
        val dy = Math.abs(motionTouchEventY - currentY)
        if(dx>=touchTolerance || dy>=touchTolerance)
        {
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching control point (x1,y1), and ending at (x2,y2).
            path.quadTo(currentX, currentY, motionTouchEventX , motionTouchEventY)
            currentX = motionTouchEventX
            currentY = motionTouchEventY
            // Draw the path in the extra bitmap to cache it.
            extraCanvas.drawPath(path, paint)
        }
        invalidate()
    }
    private  fun touchStart(){
        path.reset()
        path.moveTo(motionTouchEventX,motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }






    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (::extraBitmap.isInitialized) extraBitmap.recycle()

        extraBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(extraBitmap,0f,0f,null)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
       motionTouchEventX = event.x
       motionTouchEventY  = event.y
        when(event.action)
        {
            MotionEvent.ACTION_UP-> touchUp()
            MotionEvent.ACTION_MOVE->touchMove()
            MotionEvent.ACTION_DOWN -> touchStart()
        }
        return true
    }
}