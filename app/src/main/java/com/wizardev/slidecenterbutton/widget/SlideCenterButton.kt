package com.wizardev.slidecenterbutton.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.wizardev.slidecenterbutton.R

/**
 * founter：符乃辉
 * time：2019/2/27
 * email:wizarddev@163.com
 * description:
 */
class SlideCenterButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var mBackgroundPaint: Paint
    private lateinit var mSnakePaint: Paint
    private lateinit var mSnakeTextPaint: Paint
    private lateinit var mInnerTextPaint: Paint
    private var backgroundColor: Int? = null
    private var snakeColor: Int? = null
    private var snakeTextColor: Int? = null
    private var snakeInnerTextColor: Int? = null
    private var mSnakeRadius: Int = 0
    private var snakeTextSize: Int = 0
    private var snakeInnerTextSize: Float = 0F
    var slideX = 0f
    private var leftContent = ""
    private var rightContent = ""


    private var initLeftText: String? = null
    private var initRightText: String? = null
    private var changeLeftText: String? = null
    private var changeRightText: String? = null


    private var mShadowRadius = 20f
    private var state = 1


    var mSlideState: SlideState = SlideState.INIT


    private var slideListener: OnSlideListener? = null


    private var mSnakeBarTopText: String? = ""
    private var mSnakeBarBottomText: String? = ""

    init {

        //初始化视图
        initView(context, attrs, defStyleAttr)
        if (mShadowRadius != 0f) {
            //禁止硬件加速
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }


        initPaint()

    }

    private fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.SlideCenterButton, defStyleAttr, 0)
        //背景颜色
        backgroundColor =
            typeArray.getColor(R.styleable.SlideCenterButton_buttonBackgroundColor, resources.getColor(R.color.white))

        //中间按钮的颜色
        snakeColor =
            typeArray.getColor(R.styleable.SlideCenterButton_centerSnakeColor, resources.getColor(R.color.white))

        //滑动框内文字的颜色
        snakeInnerTextColor =
            typeArray.getColor(R.styleable.SlideCenterButton_buttonInnerTextColor, resources.getColor(R.color.white))

        //按钮的半径
        mSnakeRadius = typeArray.getDimensionPixelSize(R.styleable.SlideCenterButton_centerSnakeRadius, 8)

        //滑动范围内文字的大小
        snakeInnerTextSize = typeArray.getDimension(R.styleable.SlideCenterButton_buttonInnerTextSize, 12f)

        //中间按钮文字的颜色
        snakeTextColor =
            typeArray.getColor(R.styleable.SlideCenterButton_centerSnakeBarTextColor, resources.getColor(R.color.white))

        //中间按钮文字的大小
        snakeTextSize = typeArray.getDimensionPixelSize(R.styleable.SlideCenterButton_centerSnakeBarTextSize, 12)

        //中间按钮上部的文字
        mSnakeBarTopText = typeArray.getString(R.styleable.SlideCenterButton_centerSnakeBarTopText)
        //中间按钮下部的文字
        mSnakeBarBottomText = typeArray.getString(R.styleable.SlideCenterButton_centerSnakeBarBottomText)

        //设置初始状态，按钮位于中间还是左边
        state = typeArray.getInt(R.styleable.SlideCenterButton_centerButtonType, 1)

        initLeftText = typeArray.getString(R.styleable.SlideCenterButton_buttonInitLeftText)
        initRightText = typeArray.getString(R.styleable.SlideCenterButton_buttonInitRightText)

        changeLeftText = typeArray.getString(R.styleable.SlideCenterButton_buttonChangeLeftText)
        changeRightText = typeArray.getString(R.styleable.SlideCenterButton_buttonChangeRightText)

        mShadowRadius = typeArray.getDimensionPixelSize(R.styleable.SlideCenterButton_buttonShadowRadius, 0).toFloat()


        setLeftContent(initLeftText)
        setRightContent(initRightText)

        mSlideState = if (state == 1) {
            SlideState.INIT

        } else {
            SlideState.INIT_LEFT
        }

        typeArray.recycle()
    }

    private fun initPaint() {
        mBackgroundPaint = Paint()
        mBackgroundPaint.run {
            color = backgroundColor!!
            style = Paint.Style.FILL
            setShadowLayer(mShadowRadius, 0f, 4f, Color.RED)
        }

        mSnakePaint = Paint()
        mSnakePaint.run {
            color = snakeColor!!
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        //中间按键文字的画笔
        mSnakeTextPaint = Paint()
        mSnakeTextPaint.run {
            color = snakeTextColor!!
            textSize = snakeTextSize.toFloat()
            textAlign = Paint.Align.CENTER
            style = Paint.Style.FILL
            isAntiAlias = true

        }

        //中间文字的画笔
        mInnerTextPaint = Paint()
        mInnerTextPaint.run {
            color = snakeInnerTextColor!!
            textSize = snakeInnerTextSize
            textAlign = Paint.Align.CENTER
            style = Paint.Style.FILL
            isAntiAlias = true
        }

    }

    private var mResultWidth: Int = 0

    private var mResultHeight: Int = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        mResultWidth = widthSize
        mResultHeight = heightSize
        if (widthMode == View.MeasureSpec.AT_MOST) {
            val contentWidth = mSnakeRadius * 2 + paddingLeft + paddingRight + mShadowRadius
            mResultWidth = if (contentWidth < widthSize) contentWidth.toInt() else mResultWidth
        }

        if (heightMode == View.MeasureSpec.AT_MOST) {
            val contentHeight =
                mSnakeRadius * 2 + paddingTop + paddingBottom + mShadowRadius
            mResultHeight = if (contentHeight < heightSize) contentHeight.toInt() else mResultHeight
        }

        setMeasuredDimension(mResultWidth, mResultHeight)
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas) {

        //画背景
        drawBackground(canvas)
        //画中间的按钮
        drawSnake(canvas)
        //画右边的文字
        drawInnerText(canvas)
        //画中心按钮的文字
        drawCenterText(canvas)
    }

    private fun drawCenterText(canvas: Canvas) {
        val snakeBaselineTop: Float
        val snakeBaselineBottom: Float
        val snakeFontMetrics = mSnakeTextPaint.fontMetrics
        snakeBaselineTop = mResultHeight / 2 - snakeFontMetrics.descent
        snakeBaselineBottom = mResultHeight / 2 - snakeFontMetrics.ascent

        val centerTextX = if (slideX == 0f) {
            if (mSlideState == SlideState.INIT) {
                (mResultWidth / 2).toFloat()

            } else {
                mShadowRadius / 2 + mSnakeRadius
            }
        } else {
            slideX
        }
        if (mSnakeBarTopText.isNullOrEmpty()) {
            mSnakeBarTopText = ""
        }

        if (mSnakeBarBottomText.isNullOrEmpty()) {
            mSnakeBarBottomText = ""
        }
        canvas.drawText(mSnakeBarTopText!!, centerTextX, snakeBaselineTop, mSnakeTextPaint)
        canvas.drawText(mSnakeBarBottomText!!, centerTextX, snakeBaselineBottom, mSnakeTextPaint)
    }

    private fun drawInnerText(canvas: Canvas) {

        canvas.save()
        canvas.clipRect(
            mShadowRadius,
            mShadowRadius,
            mResultWidth.toFloat() - mShadowRadius,
            mResultHeight.toFloat() - mShadowRadius
        )
        val fontMetrics = mInnerTextPaint.fontMetrics

        //画圆环内的文字
        val baseline = mResultHeight / 2 + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        //文字的起始位置
        val halfResultWidth = mResultWidth / 2
        val start = (halfResultWidth - mSnakeRadius) / 2
        //文字的最终位置
//        val end = (mResultWidth - 2 * mSnakeRadius) / 2
        val end = mResultWidth / 2
        //距离
        val distance = end - start
        var proportion = if (mSlideState == SlideState.INIT_LEFT) {
            -1f

        } else {
            0f

        }

        if (slideX != 0f) {
            //计算比例来移动文字的距离
            proportion = (slideX - halfResultWidth) / (mResultWidth - mSnakeRadius - halfResultWidth - mShadowRadius / 2)

        }


        val resultLeftX = start + distance * proportion

        canvas.drawText(
            leftContent,
            resultLeftX,
            baseline,
            mInnerTextPaint
        )
        val resultRightX =
            halfResultWidth + (mSnakeRadius / 2) + (halfResultWidth / 2) + (distance * proportion)
        canvas.drawText(
            rightContent,
            resultRightX,
            baseline,
            mInnerTextPaint
        )
        canvas.restore()
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun drawBackground(canvas: Canvas) {
        canvas.drawRoundRect(
            mShadowRadius,
            mShadowRadius,
            mResultWidth.toFloat() - mShadowRadius,
            mResultHeight.toFloat() - mShadowRadius,
            mSnakeRadius.toFloat(),
            mSnakeRadius.toFloat(),
            mBackgroundPaint
        )
    }

    private fun drawSnake(canvas: Canvas) {
        val circleRadius = mSnakeRadius.toFloat() - mShadowRadius / 2

        val circleCenter = if (slideX == 0f) {
            if (mSlideState == SlideState.INIT_LEFT) {
                mSnakeRadius + mShadowRadius / 2
            } else {
                mResultWidth / 2.toFloat()

            }
        } else {
            slideX
        }

        canvas.drawCircle(circleCenter, mResultHeight / 2.toFloat(), circleRadius, mSnakePaint)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //滑动完成后禁止滑动
        if (mSlideState == SlideState.LEFT_FINISH || mSlideState == SlideState.RIGHT_FINISH) {
            return false
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val clickX = event.x
                val clickY = event.y
                //判断clickX的位置是否在中间的按钮内
                if (mSlideState == SlideState.INIT) {
                    return if (slideX == 0f) {
                        mResultWidth / 2 - mSnakeRadius <= clickX && clickX <= mResultWidth / 2 + mSnakeRadius

                    } else {
                        slideX - mSnakeRadius <= clickX && clickX <= slideX + mSnakeRadius
                    }
                } else if (mSlideState == SlideState.INIT_LEFT) {
                    return if (slideX == 0f) {
                        mShadowRadius / 2 <= clickX && clickX <= mShadowRadius / 2 + 2 * mSnakeRadius

                    } else {
                        slideX - mSnakeRadius <= clickX && clickX <= slideX + mSnakeRadius
                    }
                }

            }


            MotionEvent.ACTION_MOVE -> {
                slideX = event.x
                val slideY = event.y

                if (slideY > mResultHeight || slideY < mResultHeight / 2 - mSnakeRadius) {

                }

                if (slideX > mResultWidth - mSnakeRadius - mShadowRadius / 2) {
                    slideX = mResultWidth - mSnakeRadius-mShadowRadius / 2
                } else if (slideX < mSnakeRadius + mShadowRadius / 2) {
                    slideX = mSnakeRadius+ mShadowRadius / 2
                }
                if (mSlideState == SlideState.INIT) {

                    val center = mResultWidth / 2
                    if (slideX > center) {

                        setLeftContent(changeLeftText)
                        setRightContent(initRightText)

                    } else if (slideX < center) {

                        setLeftContent(initLeftText)
                        setRightContent(changeRightText)

                    }
                }

                postInvalidate()

            }

            MotionEvent.ACTION_UP -> {
                val center = mResultWidth / 2

                if (mSlideState == SlideState.INIT) {
                    if (slideX > center) {

                        if (slideX > mResultWidth - 2 * mSnakeRadius - mShadowRadius) {
                            //直接滑倒目标点
                            slideAnimate(slideX.toInt(), (mResultWidth - mSnakeRadius - mShadowRadius / 2).toInt())
                            mSlideState = SlideState.RIGHT_FINISH
                            if (slideListener != null) {
                                slideListener!!.onSlideRightFinish()
                            }
                        } else {
                            setInitText()
                            slideAnimate(slideX.toInt(), center)

                        }

                    } else if (slideX < center) {
                        if (slideX < 2 * mSnakeRadius + mShadowRadius) {
                            //直接滑倒目标点
                            slideAnimate(slideX.toInt(), (mSnakeRadius + mShadowRadius / 2).toInt())
                            mSlideState = SlideState.LEFT_FINISH
                            if (slideListener != null) {
                                slideListener!!.onSlideLiftFinish()
                            }

                        } else {
                            setInitText()

                            slideAnimate(slideX.toInt(), center)

                        }
                    } else {
                        //松手后回到原点
                        slideAnimate(slideX.toInt(), center)
                    }
                } else if (mSlideState == SlideState.INIT_LEFT) {
                    if (slideX < center) {

                        slideAnimate(slideX.toInt(), (mShadowRadius / 2 + mSnakeRadius).toInt())
                    } else {
                        mSlideState = SlideState.LEFT_FINISH
                        slideAnimate(slideX.toInt(), (mResultWidth - mSnakeRadius - mShadowRadius / 2).toInt())

                    }
                }

            }
        }
        return super.onTouchEvent(event)
    }

    private fun setInitText() {
        setLeftContent(
            content = if (initLeftText.isNullOrEmpty()) {
                ""
            } else {
                initLeftText
            }
        )

        setRightContent(
            content = if (initRightText.isNullOrEmpty()) {
                ""
            } else {
                initRightText
            }
        )
    }


    fun setLeftContent(content: String?) {

        leftContent = if (content.isNullOrEmpty()) {
            ""
        } else {
            content
        }
        postInvalidate()
    }

    fun setRightContent(content: String?) {

        rightContent = if (content.isNullOrEmpty()) {
            ""
        } else {
            content
        }
        postInvalidate()

    }


    private fun slideAnimate(start: Int, end: Int) {
        val valueAnimator = ValueAnimator.ofInt(start, end)
        valueAnimator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            slideX = animatedValue.toFloat()
            postInvalidate()
        }
        valueAnimator.start()
    }

    //按钮的状态
    enum class SlideState {
        INIT,
        LEFT_FINISH,
        RIGHT_FINISH,
        INIT_LEFT
    }

    //设置按钮的状态
    fun setSlideState(state: SlideState) {
        mSlideState = state
        postInvalidate()
    }

    interface OnSlideListener {

        fun onSlideLiftFinish()

        fun onSlideRightFinish()
    }

    //设置监听
    fun setOnSlideListener(onSlideListener: OnSlideListener) {
        slideListener = onSlideListener
    }
}