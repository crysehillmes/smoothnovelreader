package org.cryse.novelreader.util.animation;

import android.animation.Animator;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

import org.cryse.novelreader.util.LayerEnablingAnimatorListener;

public class SlideInOutAnimator {
    public static final int DURATION_SLIDE_IN = 500;
    public static final int DURATION_SLIDE_OUT = 300;

    public static void slideInToTop(
            Context context,
            View view,
            boolean animated,
            Runnable runOnAnimationEnd
    ) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        slideInToTop(
                context,
                view,
                metrics.heightPixels,
                0,
                0f,
                1f,
                animated,
                runOnAnimationEnd
        );
    }

    public static void slideInToTop(
            Context context,
            View view,
            int startY,
            int endTranslationY,
            float startAlpha,
            float endAlpha,
            boolean animated,
            Runnable runOnAnimationEnd
    ) {
        view.setTranslationY(startY);
        view.setAlpha(startAlpha);
        if (!view.isShown())
            view.setVisibility(View.VISIBLE);
        view.animate().
                translationY(endTranslationY).
                alpha(endAlpha).
                setDuration(animated ? DURATION_SLIDE_IN : 0).
                setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new LayerEnablingAnimatorListener(view) {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (runOnAnimationEnd != null)
                            runOnAnimationEnd.run();
                    }
                });
    }

    public static void slideOutToButtom(
            Context context,
            View view,
            boolean animated,
            Runnable runOnAnimationEnd
    ) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        slideOutToButtom(
                context,
                view,
                0,
                metrics.heightPixels,
                1f,
                0f,
                animated,
                runOnAnimationEnd);
    }

    public static void slideOutToButtom(
            Context context,
            View view,
            int startY,
            int translationY,
            float startAlpha,
            float endAlpha,
            boolean animated,
            Runnable runOnAnimationEnd
    ) {
        view.setTranslationY(startY);
        view.setAlpha(startAlpha);
        view.animate().
                translationY(translationY).
                alpha(endAlpha).
                setDuration(animated ? DURATION_SLIDE_OUT : 0).
                setInterpolator(new AccelerateInterpolator())
                .setListener(new LayerEnablingAnimatorListener(view) {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.INVISIBLE);
                        if (runOnAnimationEnd != null)
                            runOnAnimationEnd.run();
                    }

                });
    }
}
