package me.fruitman.sprint.presentation

import android.graphics.Canvas
import android.graphics.Point
import android.view.View

class DragTaskShadowBuilder(private val v: View) : View.DragShadowBuilder(v) {
    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        size.set(view.width, view.height)
        touch.set(100, 100)
    }

    override fun onDrawShadow(canvas: Canvas) = v.draw(canvas)
}