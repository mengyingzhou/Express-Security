package com.comyou.QRScan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class ScanLineView extends View {
    Paint paint = new Paint();

    private float density;// 屏幕密度
    private float width, height;// 控件的宽度高度

    public ScanLineView(Context context) {
        super(context);
        init();
    }

    public ScanLineView(Context context, AttributeSet attrs, int defStyleAttr,
                        int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public ScanLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ScanLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        density = getResources().getDisplayMetrics().density;
        // 去锯齿
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#316e9b"));


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;

        } else {
            width = (int) (250 * density);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;

        } else {
            height = (int) (250 * density);
        }

        // Log.e("tag", " ------ width:"+width +"   height:"+height);
        setMeasuredDimension((int) this.width, (int) this.height);

        Shader mShader = new LinearGradient(width / 2.0f, height, width / 2.0f, 0, new int[]{
                getResources().getColor(R.color.scran_blue), Color.TRANSPARENT}, null,
                Shader.TileMode.CLAMP);
        paint.setShader(mShader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 为Paint设置渐变器

        // 绘制矩形
        canvas.drawRect(0, 0, width, height, paint);
    }
}
