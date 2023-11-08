package site.kotty_kov.powertodo.todolist.main.notepad

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet

class LinedEditText(context: Context, attrs: AttributeSet) :
    androidx.appcompat.widget.AppCompatEditText(context, attrs) {

    private val mRect: Rect = Rect()
    private val mPaint: Paint

    init {
        mPaint = Paint()
        mPaint.setStyle(Paint.Style.STROKE)
        mPaint.setColor(-0x7fffff01)
    }


    override fun onDraw(canvas: Canvas) {
        val count = lineCount
        val r: Rect = mRect
        val paint: Paint = mPaint
        for (i in 0 until count) {
            val baseline = getLineBounds(i, r)
            canvas.drawLine(r.left.toFloat(), (baseline + 1).toFloat(),
                r.right.toFloat(), (baseline + 1).toFloat(), paint)
        }
        super.onDraw(canvas)
    }



}