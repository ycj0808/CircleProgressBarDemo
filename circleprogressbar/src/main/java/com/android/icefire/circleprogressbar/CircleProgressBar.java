package com.android.icefire.circleprogressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import java.text.DecimalFormat;

/**
 * 环形进度条
 * Created by yangchj on 2016/8/19 0019.
 * email:yangchj@neusoft.com
 */
public class CircleProgressBar extends View {

    private RectF mRectF = new RectF();//定义一个矩形
    private Paint mWheelPaint;
    private Paint mColorWheelPaint;
    private Paint mColorWheelPaintCenter;

    private float cicleStrokeWidth;
    private float mSweepAnglePer;
    private float mPercent;
    private int stepnumber, stepnumbernow;
    private float pressExtraStrokeWidth;

    private BarAnimation anim;
    private int stepnumbermax = 12;// 默认最大时间
    private DecimalFormat fnum = new DecimalFormat("#.0");// 格式为保留小数点后一位

    private int mColors[];
    private Context context;

    public CircleProgressBar(Context context) {
        this(context,null);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mColors=new int[]{0xFF6BB7ED,0xFF47D9CE,0xFF56CADC};
        Shader shader=new SweepGradient(0,0,mColors,null);
        mColorWheelPaint=new Paint();
        mColorWheelPaint.setShader(shader);
        mColorWheelPaint.setStyle(Paint.Style.STROKE);
        mColorWheelPaint.setStrokeCap(Paint.Cap.ROUND);
        mColorWheelPaint.setAntiAlias(true);

        mColorWheelPaintCenter=new Paint();
        mColorWheelPaintCenter.setColor(Color.rgb(214, 246, 254));
        mColorWheelPaintCenter.setStyle(Paint.Style.STROKE);
        mColorWheelPaintCenter.setStrokeCap(Paint.Cap.ROUND);
        mColorWheelPaintCenter.setAntiAlias(true);

        mWheelPaint=new Paint();
        mWheelPaint.setColor(Color.rgb(127,127,127));
        mWheelPaint.setStyle(Paint.Style.STROKE);
        mWheelPaint.setStrokeCap(Paint.Cap.ROUND);
        mWheelPaint.setAntiAlias(true);
        anim=new BarAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /**
         * drawArc (RectF oval, float startAngle, float sweepAngle, boolean useCenter, Paint paint)
         * oval是RecF类型的对象，其定义了椭圆的形状
         * startAngle指的是绘制的起始角度，钟表的3点位置对应着0度，如果传入的startAngle小于0或者大于等于360，那么用startAngle对360进行取模后作为起始绘制角度。
         * sweepAngle指的是从startAngle开始沿着钟表的顺时针方向旋转扫过的角度。如果sweepAngle大于等于360，那么会绘制完整的椭圆弧。如果sweepAngle小于0，那么会用sweepAngle对360进行取模后作为扫过的角度。
         * useCenter是个boolean值，如果为true，表示在绘制完弧之后，用椭圆的中心点连接弧上的起点和终点以闭合弧；如果值为false，表示在绘制完弧之后，弧的起点和终点直接连接，不经过椭圆的中心点。
         */
         canvas.drawArc(mRectF,0,359,false,mWheelPaint);
         canvas.drawArc(mRectF,0,359,false,mColorWheelPaintCenter);
         canvas.drawArc(mRectF,270,mSweepAnglePer,false,mColorWheelPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height=getDefaultSize(getSuggestedMinimumHeight(),heightMeasureSpec);
        int width=getDefaultSize(getSuggestedMinimumWidth(),widthMeasureSpec);
        int min=Math.min(width,height);//获取View最短边距离
        setMeasuredDimension(min,min);//强制改View为最短边长度的正方形
        cicleStrokeWidth=textScale(35,min);//圆弧的宽度
        pressExtraStrokeWidth=textScale(2,min);//圆弧离矩形的距离
        mRectF.set(cicleStrokeWidth+pressExtraStrokeWidth,cicleStrokeWidth+pressExtraStrokeWidth,min-cicleStrokeWidth-pressExtraStrokeWidth,min-cicleStrokeWidth-pressExtraStrokeWidth);//设置矩形

        mColorWheelPaint.setStrokeWidth(cicleStrokeWidth-5);
        mColorWheelPaintCenter.setStrokeWidth(cicleStrokeWidth+5);
        mWheelPaint.setStrokeWidth(cicleStrokeWidth-textScale(2,min));
        mWheelPaint.setShadowLayer(textScale(10,min),0,0,Color.rgb(127,127,127));//设置阴影
    }

    /**
     * 进度条动画
     */
    public class BarAnimation extends Animation {
        public BarAnimation() {
        }

        /**
         * 每次系统调用这个方法时，改变mSweepAnglePer,mPercent,stepnumbernow的值
         * 然后调用postInvalidate()不停的绘制View
         *
         * @param interpolatedTime
         * @param t
         */
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1.0f) {
                mPercent = Float.parseFloat(fnum.format(interpolatedTime * stepnumber * 100f / stepnumbermax));//四舍五入保留一位小数
                mSweepAnglePer = interpolatedTime * stepnumber * 360 / stepnumbermax;
                stepnumbernow = (int) (interpolatedTime * stepnumber);
            } else {
                mPercent = Float.parseFloat(fnum.format(stepnumber * 100f / stepnumbermax));
                mSweepAnglePer = stepnumber * 360 / stepnumbermax;
                stepnumbernow = stepnumber;
            }
            postInvalidate();
        }
    }

    /**
     * 根据控件的大小改变绝对位置的比例
     * @param n
     * @param m
     * @return
     */
    public float textScale(float n, float m) {
        return n / 500 * m;
    }

    /**
     * 更新步数和设置一圈动画时间
     * @param step
     * @param time
     */
    public void update(int step,int time){
        stepnumber=step;
        anim.setDuration(time);
        this.startAnimation(anim);
    }

    /**
     * 设置最大步数
     * @param maxtStepnumber
     */
    public void setMaxstepnumber(int maxtStepnumber){
        stepnumbermax=maxtStepnumber;
    }

    /**
     * 设置进度条颜色
     * @param red
     * @param green
     * @param blue
     */
    public void setColor(int red,int green,int blue){
        mColorWheelPaint.setColor(Color.rgb(red,green,blue));
    }
    /**
     * 按照比例设置动画时间
     * @param time
     */
    public void setAnimationTime(int time){
        anim.setDuration(time*stepnumber/stepnumbermax);
    }
}
