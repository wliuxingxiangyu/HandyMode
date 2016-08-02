package com.example.hz.handymode;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by mi on 16-8-2.
 */
public class KeyButtonRipple extends Drawable {

    private static final float GLOW_MAX_SCALE_FACTOR = 1.35f;
    private static final float GLOW_MAX_ALPHA = 0.25f;
    private static final int ANIMATION_DURATION_SCALE = 350;
    private static final int ANIMATION_DURATION_FADE = 450;

    private Paint mRipplePaint;
    private float mGlowAlpha = 0f;
    private float mGlowScale = 1f;
    private boolean mPressed;
    private boolean mDrawingHardwareGlow;
    private int mMaxWidth;

    private final Interpolator mInterpolator = new LogInterpolator();
    private final Interpolator mAlphaExitInterpolator = new PathInterpolator(0f, 0f, 0.8f, 1f);
    private boolean mSupportHardware;
    private final View mTargetView;

    private final HashSet<Animator> mRunningAnimations = new HashSet<>();
    private final ArrayList<Animator> mTmpArray = new ArrayList<>();

    public KeyButtonRipple(Context ctx, View targetView) {
        mMaxWidth = ctx.getResources().getDimensionPixelSize(R.dimen.key_button_ripple_max_width);
        mTargetView = targetView;
    }

    private Paint getRipplePaint() {
        if (mRipplePaint == null) {
            mRipplePaint = new Paint();
            mRipplePaint.setAntiAlias(true);
            mRipplePaint.setColor(0xffcccccc);
        }
        return mRipplePaint;
    }

    private void drawSoftware(Canvas canvas) {
        if (mGlowAlpha > 0f) {

            final Paint p = getRipplePaint();//涟漪
            p.setAlpha((int) (mGlowAlpha * 255f));//透明度mGlowAlpha为0
            final float w = getBounds().width();
            final float h = getBounds().height();
            final boolean horizontal = w > h;
            final float diameter = getRippleSize() * mGlowScale;//直径=原大小×缩放mGlowScale=1.
            final float radius = diameter * .5f;//半径
            final float cx = w * .5f;
            final float cy = h * .5f;
            final float rx = horizontal ? radius : cx;//水平rx=radius
            final float ry = horizontal ? cy : radius;//水平ry=cy
            final float corner = horizontal ? cy : cx;
            Log.e("drawSoftware()--","mGlowAlpha="+mGlowAlpha+"--w="+w+"--h="+h+"--rx="+rx+"--ry="+ry+"--corner"+corner);
//            canvas.drawRoundRect(cx - rx, cy - ry, cx + rx, cy + ry, corner, corner, p);
//            Log.e("drawSoftware()--","cx-rx="+(cx-rx)+"--(cy-ry)="+(cy-ry)+
//                    "--(cx+rx)="+(cx+rx)+"--(cy+ry)="+(cy+ry)+"--corner="+corner);

            //cx-rx=82.5--(cy-ry)=0.0--(cx+rx)=82.5--(cy+ry)=53.0--corner=26.5
            //cx-rx=-28.849655--(cy-ry)=0.0--(cx+rx)=193.84966--(cy+ry)=53.0--corner=26.5

            canvas.drawRoundRect(10f,20f,40f,80f,10f,50f,p);//左顶右下，
        }
    }

    @Override
    public void draw(Canvas canvas) {
        drawSoftware(canvas);
    }

    @Override
    public void setAlpha(int alpha) {
        // Not supported.
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        // Not supported.
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    private boolean isHorizontal() {
        return getBounds().width() > getBounds().height();
    }

    public float getGlowAlpha() {
        return mGlowAlpha;
    }

    public void setGlowAlpha(float x) {
        mGlowAlpha = x;
        invalidateSelf();
    }

    public float getGlowScale() {
        return mGlowScale;
    }

    public void setGlowScale(float x) {
        mGlowScale = x;
        invalidateSelf();
    }

    @Override
    protected boolean onStateChange(int[] state) {
        boolean pressed = false;
        for (int i = 0; i < state.length; i++) {
            if (state[i] == android.R.attr.state_pressed) {
                pressed = true;
                break;
            }
        }
        if (pressed != mPressed) {
            setPressed(pressed);
            mPressed = pressed;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void jumpToCurrentState() {
        cancelAnimations();
    }

    @Override
    public boolean isStateful() {
        return true;
    }

    public void setPressed(boolean pressed) {
        setPressedSoftware(pressed);
    }

    private void cancelAnimations() {
        mTmpArray.addAll(mRunningAnimations);
        int size = mTmpArray.size();
        for (int i = 0; i < size; i++) {
            Animator a = mTmpArray.get(i);
            a.cancel();
        }
        mTmpArray.clear();
        mRunningAnimations.clear();
    }

    public void setPressedSoftware(boolean pressed) {
        if (pressed) {
            enterSoftware();
        } else {
            exitSoftware();
        }
    }

    private void enterSoftware() {
        cancelAnimations();
        mGlowAlpha = GLOW_MAX_ALPHA;
        ObjectAnimator scaleAnimator = ObjectAnimator.ofFloat(this, "glowScale",//KeyButtonRipple类有glowScale属性。
                0f, GLOW_MAX_SCALE_FACTOR);
        scaleAnimator.setInterpolator(mInterpolator);
        scaleAnimator.setDuration(ANIMATION_DURATION_SCALE);
        scaleAnimator.addListener(mAnimatorListener);
        scaleAnimator.start();
        mRunningAnimations.add(scaleAnimator);
    }

    private void exitSoftware() {
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(this, "glowAlpha", mGlowAlpha, 0f);
        alphaAnimator.setInterpolator(mAlphaExitInterpolator);
        alphaAnimator.setDuration(ANIMATION_DURATION_FADE);
        alphaAnimator.addListener(mAnimatorListener);
        alphaAnimator.start();
        mRunningAnimations.add(alphaAnimator);
    }

    private int getExtendSize() {
        return isHorizontal() ? getBounds().width() : getBounds().height();
    }

    private int getRippleSize() {
        int size = isHorizontal() ? getBounds().width() : getBounds().height();
        return Math.min(size, mMaxWidth);
    }

    private final AnimatorListenerAdapter mAnimatorListener =
            new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRunningAnimations.remove(animation);
                    if (mRunningAnimations.isEmpty() && !mPressed) {
                        mDrawingHardwareGlow = false;
                        invalidateSelf();
                    }
                }
            };

    /**
     * Interpolator with a smooth log deceleration
     */
    private static final class LogInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float input) {
            return 1 - (float) Math.pow(400, -input * 1.4);
        }
    }
}
