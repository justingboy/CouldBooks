package com.himoo.ydsc.animation.openbook;


import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.util.DeviceUtil;

/**
 * 说明：BookView在被点击时会在WindowManager中添加一个AbsoluteLayout,然后再添加一个克隆体cover,在根据位置放置一个content。  ps:cover书的封面   content书的内容页
 * cover播放放大翻转动画  content播放放大动画
 * 再次点击，播放关闭书本动画
 *
 */
public class BookView extends ImageView implements Animation.AnimationListener{
    private boolean mIsOpen;
    private WindowManager mWindowManager;
    private AbsoluteLayout wmRootView;

    private ImageView cover;
    private ImageView content;

    private float scaleTimes;
    public static final int ANIMATION_DURATION = 1000;
    private int[] location = new int[2];

    private ContentScaleAnimation contentAnimation;
    private Rotate3DAnimation coverAnimation;

    private boolean isFirstload = true;
    private int animationCount=0;  //动画加载计数器  0 默认  1一个动画执行完毕   2二个动画执行完毕
    //todo  动画播完后要进行处理
    private Context mContext;
    
    public BookView(Context context) {
        this(context, null);
        this.mContext = context;
    }

    public BookView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.mContext = context;
    }

    public BookView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        initView();
        initListener();
    }

    void initView() {
        wmRootView = new AbsoluteLayout(getContext());
    }

    void initListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsOpen) {
                    openBook();
                }
            }
        });
    }

    void initAnimation() {
        AccelerateInterpolator interpolator=new AccelerateInterpolator();
        
        getLocationInWindow(location);

        float scale1 = DeviceUtil.getWidth((Activity)mContext) / (float) getWidth();
        float scale2 = DeviceUtil.getHeight((Activity)mContext)/ (float) getHeight();
        scaleTimes = scale1 > scale2 ? scale1 : scale2;  //计算缩放比例
        
        contentAnimation = new ContentScaleAnimation(1, scaleTimes, 1, scaleTimes, location[0], location[1], false);
        contentAnimation.setInterpolator(interpolator);
        contentAnimation.setDuration(ANIMATION_DURATION);
        contentAnimation.setFillAfter(true);
        contentAnimation.setAnimationListener(this);


        coverAnimation = new Rotate3DAnimation(0, -180, location[0], location[1], scaleTimes, false);
        coverAnimation.setInterpolator(interpolator);
        coverAnimation.setDuration(ANIMATION_DURATION);
        coverAnimation.setFillAfter(true);
        coverAnimation.setAnimationListener(this);

    
    }

    public void openBook() {
        if(isFirstload){
            isFirstload=false;
            
            initAnimation();
        }

        mWindowManager.addView(wmRootView, getDefaultWindowParams());

        cover = new ImageView(getContext());
        cover.setScaleType(getScaleType());
        cover.setImageDrawable(getDrawable());

        content = new ImageView(getContext());
        content.setScaleType(getScaleType());
        content.setBackground(getResources().getDrawable(R.drawable.book_face_default));

        ViewGroup.LayoutParams params = getLayoutParams();
        wmRootView.addView(content, params);
        wmRootView.addView(cover, params);
        cover.setX(location[0]);
        cover.setY(location[1]);
        content.setX(location[0]);
        content.setY(location[1]);

        //一个不合理的方案，把关闭书本动画放到这
        wmRootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsOpen){
                    closeBook();
                }
            }
        });


        if (contentAnimation.getMReverse()) {
            contentAnimation.reverse();
        }

        if (coverAnimation.getMReverse()) {
            coverAnimation.reverse();
        }

        content.clearAnimation();
        content.startAnimation(contentAnimation);
        cover.clearAnimation();
        cover.startAnimation(coverAnimation);
    }

    public void closeBook() {
        if (!contentAnimation.getMReverse()) {
            contentAnimation.reverse();
        }

        if (!coverAnimation.getMReverse()) {
            coverAnimation.reverse();
        }

        content.clearAnimation();
        content.startAnimation(contentAnimation);
        cover.clearAnimation();
        cover.startAnimation(coverAnimation);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private WindowManager.LayoutParams getDefaultWindowParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                0, 0,
                WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                PixelFormat.RGBA_8888);

        return params;
    }


    @Override
    public void onAnimationStart(Animation animation) {
        
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(!mIsOpen){
            animationCount++;

            if(animationCount>=2) {
                mIsOpen = true;
            }
        }else{
            animationCount--;

            if(animationCount<=0) {
                mIsOpen = false;

                mWindowManager.removeView(wmRootView);

            }
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
