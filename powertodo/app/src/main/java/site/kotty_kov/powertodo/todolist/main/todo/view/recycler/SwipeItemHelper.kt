package site.kotty_kov.powertodo.todolist.main.todo.view.recycler

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class SwipeItemHelper : ItemTouchHelper.SimpleCallback {

    var onSwipeToDoListener: OnSwipeToDoListener

    private var canSwipe = true

    interface OnSwipeToDoListener {
        fun onItemDelete(pos: Int)
        fun onItemSchedule(pos: Int)
        fun isItemAllowedToSwipe(pos: Int): Boolean
    }


    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {

        return makeMovementFlags(0, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        canSwipe = false
        swipeAgainDelay()
        //right
        if (direction == 32) {
            onSwipeToDoListener.onItemSchedule(viewHolder.adapterPosition)
        }
        //left
        else if (direction == 16) {
            onSwipeToDoListener.onItemDelete(viewHolder.adapterPosition)
        }
    }

    private fun swipeAgainDelay() {
        Thread(object : Runnable {
            override fun run() {
                synchronized(this) {
                    try {
                        Thread.sleep(400) //wait for some and allow user to swipe again
                        canSwipe = true
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }).start()
    }

    //Allow dragging
    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    //Allow user to swipe again when timer run out.
    override fun isItemViewSwipeEnabled(): Boolean {
        return canSwipe
    }

    //----------------------------------------
    var swipingRightNow = 0

    //configure left swipe params
    var leftBG: Int = Color.LTGRAY
    var leftLabel: String = ""
    var leftIcon: Drawable? = null

    //configure right swipe params
    var rightBG: Int = Color.LTGRAY;
    var rightLabel: String = ""
    var rightIcon: Drawable? = null

    var textSize: Int = 15
    var compactMode: Boolean = false

    var context: Context;


    companion object {
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
    }


    constructor(context: Context, osdl: OnSwipeToDoListener) : super(0, swipeFlags) {
        this.context = context
        this.onSwipeToDoListener = osdl
    }


    private lateinit var background: Drawable

    var initiated: Boolean = false
    val paint = Paint()

    fun initSwipeView() {
        paint.setColor(Color.WHITE)
        paint.setTextSize(textSize * context.resources.displayMetrics.density)
        paint.setTextAlign(Paint.Align.CENTER)
        background = ColorDrawable();
        initiated = true;
    }


    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        val itemView = viewHolder.itemView
        if (!initiated) {
            initSwipeView()
        }


        if (dX != 0.0f) {
            // header position  -1bug
            if (viewHolder.adapterPosition < 0) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                return

            }
            //do not swipe a header
            if (!onSwipeToDoListener.isItemAllowedToSwipe(viewHolder.adapterPosition)) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                return
            }


            val pixels = context.resources.displayMetrics.density

            if (dX > 0) {
                //right swipe
                var intrinsicHeight = (rightIcon?.intrinsicWidth ?: 0)
                if (compactMode) {
                    intrinsicHeight = 0
                }
                val xMarkTop =
                    itemView.top + ((itemView.bottom - itemView.top) - intrinsicHeight) / 2
                val xMarkBottom = xMarkTop + intrinsicHeight

                colorCanvas(
                    c,
                    rightBG,
                    itemView.left,
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                drawTextOnCanvas(
                    c,
                    rightLabel,
                    (itemView.left + rightLabel.length * pixels * 5),

                    xMarkTop + pixels * 4
                )
                if (!compactMode) {
                    drawIconOnCanvas(
                        c, rightIcon,
                        (itemView.left + (rightIcon?.getIntrinsicWidth()
                            ?: 0) + pixels * 5).toInt(),
                        (xMarkTop + pixels * 5).toInt(),
                        (itemView.left + 2 * (rightIcon?.getIntrinsicWidth()
                            ?: 0) + pixels * 5).toInt(),
                        (xMarkBottom + pixels * 7).toInt()
                    )
                }
            }

            if (dX < 0) {
                //left swipe
                var intrinsicHeight = (leftIcon?.getIntrinsicWidth() ?: 0)
                if (compactMode) {
                    intrinsicHeight = 0
                }

                val xMarkTop =
                    itemView.top + ((itemView.bottom - itemView.top) - intrinsicHeight) / 2
                val xMarkBottom = xMarkTop + intrinsicHeight

                colorCanvas(
                    c,
                    leftBG,
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                drawTextOnCanvas(
                    c,
                    leftLabel,
                    (itemView.right - pixels * 50).toFloat(),
                    (xMarkTop + pixels * 4).toFloat()
                )

                if (!compactMode) {
                    drawIconOnCanvas(
                        c, leftIcon,
                        (itemView.right - 2 * (leftIcon?.getIntrinsicWidth()
                            ?: 0) - pixels * 5).toInt(),
                        (xMarkTop + pixels * 5).toInt(),
                        (itemView.right - (leftIcon?.getIntrinsicWidth()
                            ?: 0) - pixels * 5).toInt(),
                        (xMarkBottom + pixels * 5).toInt()
                    )
                }
            }
        }



        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }


    private fun colorCanvas(
        canvas: Canvas,
        canvasColor: Int,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        (background as ColorDrawable).color = canvasColor
        background.setBounds(left, top, right, bottom)
        background.draw(canvas)
    }

    private fun drawTextOnCanvas(canvas: Canvas, label: String, x: Float, y: Float) {
        canvas.drawText(label, x, y, paint)
    }

    private fun drawIconOnCanvas(
        canvas: Canvas, icon: Drawable?, left: Int, top: Int, right: Int, bottom: Int
    ) {
        icon?.setBounds(left, top, right, bottom)
        icon?.draw(canvas)

    }


}