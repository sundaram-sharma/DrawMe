package en.sundaram.drawme

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attr: AttributeSet): View(context, attr) {

    private var mDrawPath : CustomPath? = null // An variable of CustomPath inner class to use it further.
    private var mCanvasBitmap: Bitmap? = null // An instance of the bitmap
    private var mDrawPaint: Paint? = null // The Paint class holds the style and color information about how to draw geometries, text and bitmaps.
    private var mCanvasPaint: Paint? = null // Instance of canvas paint view.
    private var mBrushSize: Float = 0.toFloat() // A variable for stroke/brush size to draw on the canvas.
    private var color = Color.BLACK // A variable to hold a color of the stroke.
    private var canvas: Canvas? = null

    private val mPaths = ArrayList<CustomPath>() // to retain the path we draw
    private val mUndoPaths = ArrayList<CustomPath>() // to retain the undo paths
    //private val mRedoPaths = ArrayList<CustomPath>() // to retain the redo paths


    init {
        setUpDrawing() //code inside init will be
    }


    //Delete path from mPath and store the undo path in mUndoPath

    fun onClickUndo(){
        if(mPaths.size > 0) // only if the path exist then proceed
        {
            mUndoPaths.add(mPaths.removeAt(mPaths.size - 1)) //add the removed path to mUndoPath from mPath starting from last in array (FILO)

            //Generally, invalidate() means 'redraw on screen' and results to a call of the view's onDraw() method. So if something changes and it needs to be reflected on screen, you need to call invalidate().

            invalidate() //redraw the entire page with remaining lines
        }
    }

    fun onClickRedo(){
        if(mUndoPaths.size > 0) // only if the path exist then proceed
        {       //Only mPath can draw on screen
            mPaths.add(mUndoPaths.removeAt(mUndoPaths.size - 1)) //add the removed path to mRedoPath from mUndoPath starting from last in array (FILO)
            invalidate()

    }

    }



    /**
     * This method initializes the attributes of the
     * ViewForDrawing class.
     */
    private fun setUpDrawing() {
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color,mBrushSize)
        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)


        //mBrushSize = 20.toFloat() now we will maunally set the brush size
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) { //Called when the size of this view has changed.
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)//bitmap configuration
        canvas = Canvas(mCanvasBitmap!!) // set canvas as mCanvasBitmap

    }
        // Canvas? to canvas if fails
    /**
     * This method is called when a stroke is drawn on the canvas
     * as a part of the painting.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        /**
         * Draw the specified bitmap, with its top/left corner at (x,y), using the specified paint,
         * transformed by the current matrix.
         *
         *If the bitmap and canvas have different densities, this function will take care of
         * automatically scaling the bitmap to draw at the same density as the canvas.
         *
         * @param bitmap The bitmap to be drawn
         * @param left The position of the left side of the bitmap being drawn
         * @param top The position of the top side of the bitmap being drawn
         * @param paint The paint used to draw the bitmap (may be null)
         */
        canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f,mCanvasPaint)

        for(path in mPaths){ //
            mDrawPaint!!.strokeWidth = path.brushThickness
            mDrawPaint!!.color = path.color
            canvas.drawPath(path, mDrawPaint!!)
        }

            if(!mDrawPath!!.isEmpty) { //mDrawPath is nullable and empty
                mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness //set the brush thickness
                mDrawPaint!!.color = mDrawPath!!.color //set the color
                canvas.drawPath(mDrawPath!!, mDrawPaint!!)
            }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean { //what happen when we touch

        val touchx = event?.x //touch event for the x coordinate
        val touchy = event?.y //touch event for the y coordinate

        when (event?.action){ // '!!' for nullable
            MotionEvent.ACTION_DOWN ->{ //when we start touching the screen
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize

                mDrawPath!!.reset()  // Clear any lines and curves from the path, making it empty.
                mDrawPath!!.moveTo(touchx!!,touchy!!)


            }
            MotionEvent.ACTION_MOVE ->{ //when we drag on the screen
                mDrawPath!!.lineTo(touchx!!, touchy!!)
            }
            MotionEvent.ACTION_UP ->{ // when we stop touching the screen or pressure gesture is finished
                mPaths.add(mDrawPath!!) // this will start the drawing
                mUndoPaths.clear() //clear all the undo paths
                mDrawPath = CustomPath(color, mBrushSize)
            }

        else -> return false // in none of the above is true
        }
         invalidate() //Invalidate the whole view.

        return true
    }

    /**
     * This method is called when either the brush or the eraser
     * sizes are to be changed. This method sets the brush/eraser
     * sizes to the new values depending on user selection.
     */

    fun setSizeForBrush(newSize: Float){ //this function will set the size for the brush
        mBrushSize =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, resources.displayMetrics)
        mDrawPaint!!.strokeWidth = mBrushSize // it will retain the brush size according to the needs
    }

    fun setColor(newColor: String){
        color = Color.parseColor(newColor) //will take the color.xml value and treat them as strings
        mDrawPaint!!.color = color //set the selected color
    }

    /**
     * This method initializes the attributes of the
     * ViewForDrawing class.
     */


    internal inner class CustomPath(var color: Int, var brushThickness: Float) : Path() {




    }

}