package com.edu.happytesting.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Size
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import com.edu.happytesting.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DrawingCanva(context: Context, attrs: AttributeSet) : View(context,attrs){

    private var mDrawPath: CustomPath?=null
    private var mCanvasBitmap: Bitmap?=null
    private var mDrawPaint: Paint?=null
    private var mCanvasPaint:Paint?=null
    private var mBrushSize:Int = 0
    private var currentColor = Color.BLACK
    private var canvas: Canvas?=null
    private var mAlpha:Int=255
    private var mPaths = ArrayList<CustomPath>()
    private var mUndoPath = ArrayList<CustomPath>()



    init {
        setUpDrawing()

    }
    private var bitmapCallback: ((bitmap: Bitmap) -> Unit)? = null
    fun setBitmapCallback(callback: ((bitmap: Bitmap) -> Unit)?) {
        bitmapCallback = callback
    }

    private fun setUpDrawing() {
        mDrawPaint= Paint()
        mDrawPath=CustomPath(currentColor,mBrushSize,mAlpha)
        mDrawPaint!!.color = currentColor
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.alpha = mAlpha
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        mBrushSize =20
    }
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas =  Canvas(mCanvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(mCanvasBitmap!!,0f,0f,mCanvasPaint)
        for(path in mPaths){
            mDrawPaint!!.strokeWidth = path.brushThickness.toFloat()
            mDrawPaint!!.color=  path.color
            mDrawPaint!!.alpha = path.alpha
            canvas?.drawPath(path,mDrawPaint!!)
        }
        if(!mDrawPath!!.isEmpty){
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness.toFloat()
            mDrawPaint!!.color=  mDrawPath!!.color
            mDrawPaint!!.alpha = mDrawPath!!.alpha
            canvas?.drawPath(mDrawPath!!,mDrawPaint!!)
        }


    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y
        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                mDrawPath!!.color = currentColor
                mDrawPath!!.brushThickness = mBrushSize
                mDrawPath!!.alpha = mAlpha
                mDrawPath!!.reset()
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.moveTo(touchX,touchY)
                    }
                }
            }

            MotionEvent.ACTION_MOVE ->{
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.lineTo(touchX,touchY)
                    }
                }
            }

            MotionEvent.ACTION_UP ->{
                mPaths.add(mDrawPath!!)
                mDrawPath = CustomPath(currentColor,mBrushSize,mAlpha)


                val lifecycleScope = CoroutineScope(Dispatchers.IO)
                lifecycleScope.launch {
                    bitmapCallback?.invoke(getBitmap())
                }

            }

            else-> return false
        }

        invalidate()

        return true

    }

    @SuppressLint("SupportAnnotationUsage")
//    @IntRange(from = 0, to = 200)
    fun setSizeForBrush(newSize: Int){
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            newSize.toFloat(),resources.displayMetrics).toInt()
        mDrawPaint!!.strokeWidth = mBrushSize.toFloat()
    }




    fun erase(colorBackground: Int = Color.WHITE) {
        mBrushSize=40
        mAlpha = 255
        mDrawPaint!!.alpha = 255
        currentColor = colorBackground
        mDrawPaint!!.color = colorBackground
    }


    fun setBrushColor(color: Int){
        currentColor = color
        mDrawPaint!!.color = color
    }

    fun undo(){
        if (mPaths.size > 0){
            mUndoPath.add(mPaths[mPaths.size -1])
            mPaths.removeAt(mPaths.size -1)
            invalidate()
        }
    }
    fun redo(){
        if (mUndoPath.size >0){
            mPaths.add(mUndoPath[mUndoPath.size -1])
            mUndoPath.removeAt(mUndoPath.size -1)
            invalidate()
        }

    }
    inner class CustomPath(var color:Int , var brushThickness:Int,var alpha:Int) : Path() {
    }



    private fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }





}

