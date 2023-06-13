package com.edu.happytesting.drawing

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

class CustomScrollView(context: Context, attrs: AttributeSet) : ScrollView(context, attrs) {
    private val isScrollingEnabled = true

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        isScrollingEnabled && super.onInterceptTouchEvent(ev)

        // Allow touch events to be passed to child views for drawing
        when (ev.action) {

            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                if (ev.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS || ev.getToolType(0) == MotionEvent.TOOL_TYPE_ERASER) {
                    //pencil touch
                    setScrollEnabled(true)

                } else {
                    setScrollEnabled(false)

                }

                // Disallow interception when touch event starts
                super.onInterceptTouchEvent(ev)
                return false
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {}

        }
        return super.onInterceptTouchEvent(ev)
    }

    private val SCROLL_THRESHOLD = 0.25f

    private var isScrollEnabled = false

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        val maxScrollY = (getChildAt(0).measuredHeight - height).toFloat()
        val scrollThreshold = SCROLL_THRESHOLD * maxScrollY

        if (isScrollEnabled) {
            val scrollY = scrollThreshold.coerceAtMost(t.toFloat())
            super.onScrollChanged(l, scrollY.toInt(), oldl, oldt)
        } else {
            super.onScrollChanged(l, 0, oldl, oldt)
        }
    }

    private fun setScrollEnabled(enabled: Boolean) {
        isScrollEnabled = enabled
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return isScrollingEnabled && super.onTouchEvent(ev)
    }


}