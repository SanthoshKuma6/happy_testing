package com.edu.happytesting.drawing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class CustomDrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var path = Path()
    private lateinit var drawPath: CustomPath
    private lateinit var drawPaint: Paint
    private lateinit var canvasPaint: Paint
    private lateinit var canvasBitmap: Bitmap
    private lateinit var canvas: Canvas
    private var currentEraserSize: Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15f, resources.displayMetrics)
    private var currentXfermode: PorterDuffXfermode? = null
    private val redoPaths = ArrayList<CustomPath>()
    private val undoPaths = ArrayList<CustomPath>()

    private var currentColor: Int = Color.BLACK
    private var currentBrushSize: Float = 0.toFloat()

    companion object {
        private const val TOUCH_TOLERANCE = 4f
    }

    private var bitmap: Bitmap? = null
    private var currentX = 0f
    private var currentY = 0f

    private var bitmapCallback: ((bitmap: Bitmap) -> Unit)? = null

    init {
        isFocusable = true
        isFocusableInTouchMode = true
        setUpDrawing()
    }

    private fun setUpDrawing() {
        drawPaint = Paint()
        drawPath = CustomPath(currentColor, currentBrushSize, currentXfermode, currentEraserSize)
        canvasPaint = Paint(Paint.DITHER_FLAG)
        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.ROUND
        drawPaint.strokeCap = Paint.Cap.ROUND

    }

    fun setBitmapCallback(callback: ((bitmap: Bitmap) -> Unit)?) {
        bitmapCallback = callback
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(canvasBitmap)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        canvas.drawPath(path, paint)

        canvasBitmap.let { canvas.drawBitmap(it, 0f, 0f, canvasPaint) }

        for (path in redoPaths) {
            drawPaint.strokeWidth = path.brushThickness
            drawPaint.color = path.color
            drawPaint.xfermode = path.xFerMode
            if (path.xFerMode != null) {
                setLayerType(LAYER_TYPE_HARDWARE, null)
                drawPaint.strokeWidth = path.eraserThickness

            }
            canvas.drawPath(path, drawPaint)
        }
        drawPaint.strokeWidth = drawPath.brushThickness
        drawPaint.color = drawPath.color
        drawPaint.xfermode = drawPath.xFerMode
        if (drawPath.xFerMode != null) {
            setLayerType(LAYER_TYPE_HARDWARE, null)
            drawPaint.strokeWidth = drawPath.eraserThickness

        }
        canvas.drawPath(drawPath, drawPaint)


    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                touchStart(x, y)

                if (event.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS || event.getToolType(0) == MotionEvent.TOOL_TYPE_ERASER) {
                    //pencil touch
                    drawPath.color = currentColor
                    drawPath.brushThickness = currentBrushSize
                    drawPath.xFerMode = currentXfermode
                    drawPath.eraserThickness = currentEraserSize
                    drawPath.reset()

                    if (x != null) {
                        if (y != null) {
                            drawPath.moveTo(x, y)
                        }
                    }
                    invalidate()
                    return true
                } else {
                    //hand touch
                    increaseHeightIfNeeded()
                    return false


                }

            }

            MotionEvent.ACTION_MOVE -> {
                if (x != null) {
                    if (y != null) {
                        drawPath.lineTo(x, y)
                    }
                    invalidate()

                }

                touchMove(x, y)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                redoPaths.add(drawPath)
                undoPaths.clear()

                drawPath =
                    CustomPath(currentColor, currentBrushSize, currentXfermode, currentEraserSize)

                touchUp()
                if (!path.isEmpty) {
                    val lifecycleScope = CoroutineScope(Dispatchers.IO)
                    lifecycleScope.launch {
                        bitmapCallback?.invoke(getBitmap())
                    }
                }
            }

            else -> return false
        }

        invalidate()
        getDrawing()
        return true
    }

    private fun touchStart(x: Float, y: Float) {
        path.moveTo(x, y)
        currentX = x
        currentY = y

    }

    private fun touchMove(x: Float, y: Float) {
        val dx = abs(x - currentX)
        val dy = abs(y - currentY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(currentX, currentY, (x + currentX) / 2, (y + currentY) / 2)
            currentX = x
            currentY = y
            scrollToLastPosition()


        }
    }

    private fun increaseHeightIfNeeded() {
        val desiredHeight = 500
        val pathBounds = RectF()
        path.computeBounds(pathBounds, true)
        val lastY = pathBounds.bottom
        if (lastY >= desiredHeight) {
            val newHeight =
                (lastY + 500).toInt() // Increase the height by 300 pixels (adjust as needed)
            val newBitmap = Bitmap.createBitmap(width, newHeight, Bitmap.Config.ARGB_8888)
            val newCanvas = Canvas(newBitmap)
            newCanvas.drawColor(Color.WHITE) // Set the background color to white or any other desired color
            bitmap?.let { newCanvas.drawBitmap(it, 0f, 0f, null) }
            bitmap = newBitmap
            // Adjust the height of the view and request a layout
            val layoutParams = layoutParams
            layoutParams.height = newHeight
            setLayoutParams(layoutParams)

        }
    }

    private fun scrollToLastPosition() {
        val pathBounds = RectF()
        path.computeBounds(pathBounds, true)
        val lastX = pathBounds.right.toInt()
        val lastY = pathBounds.bottom.toInt()

        val visibleWidth = width - paddingLeft - paddingRight
        val visibleHeight = height - paddingTop - paddingBottom

        val maxXScroll = lastX - visibleWidth
        val maxYScroll = lastY - visibleHeight

        val scrollToX = max(0, min(scrollX, maxXScroll))
        val scrollToY = max(0, min(scrollY, maxYScroll))

        scrollTo(scrollToX, scrollToY)


    }

    private fun touchUp() {
        path.lineTo(currentX, currentY)
    }

    private fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap

    }

    fun onEraserSelect(size: Float) {
        currentEraserSize =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, resources.displayMetrics)
        currentXfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    fun setBrushColor(color: Int) {
        currentColor = color
        drawPaint.color = color
    }

    fun setSizeForBrush(newSize: Float) {
        currentXfermode = null
        currentBrushSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            newSize,
            resources.displayMetrics
        )
    }

    fun onClickUndo() {
        if (redoPaths.size > 0) {
            undoPaths.add(redoPaths.removeAt(redoPaths.lastIndex))
            invalidate()
            val lifecycleScope = CoroutineScope(Dispatchers.IO)
            lifecycleScope.launch {
                bitmapCallback?.invoke(getBitmap())
            }
        }
    }

    fun onClickRedo() {
        if (undoPaths.size > 0) {
            redoPaths.add(undoPaths.removeAt(undoPaths.lastIndex))
            invalidate()
            val lifecycleScope = CoroutineScope(Dispatchers.IO)
            lifecycleScope.launch {
                bitmapCallback?.invoke(getBitmap())
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = MeasureSpec.getSize(widthMeasureSpec)
        val desiredHeight = bitmap?.height ?: MeasureSpec.getSize(heightMeasureSpec)

        val width = resolveSize(desiredWidth, widthMeasureSpec)
        val height = resolveSize(desiredHeight, heightMeasureSpec)

        setMeasuredDimension(width, height)


    }

    private fun getDrawing(): ArrayList<CustomPath> {
        invalidate()
        return undoPaths

    }

    //! custom path class that has color and thickness properties which we are going to use to set
    // paint properties
    inner class CustomPath(
        var color: Int,
        var brushThickness: Float,
        var xFerMode: PorterDuffXfermode?,
        var eraserThickness: Float
    ) : Path()


}
