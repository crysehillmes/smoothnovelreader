package org.cryse.novelreader.util.animation;

import android.animation.Animator;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

import org.cryse.novelreader.util.LayerEnablingAnimatorListener;

public class FadeInOutAnimator {
    public static final int DURATION_SLIDE_IN = 500;
    public static final int DURATION_SLIDE_OUT = 300;

    public static void fadeIn(
            Context context,
            View view,
            boolean animated,
            Runnable runOnAnimationEnd
    ) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        fadeIn(
                context,
                view,
                0f,
                1f,
                animated,
                runOnAnimationEnd
        );
    }

    public static void fadeIn(
            Context context,
            View view,
            float startAlpha,
            float endAlpha,
            boolean animated,
            Runnable runOnAnimationEnd
    ) {
        view.setAlpha(startAlpha);
        if (!view.isShown())
            view.setVisibility(View.VISIBLE);
        view.animate().
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

    public static void fadeOut(
            Context context,
            View view,
            boolean animated,
            Runnable runOnAnimationEnd
    ) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        fadeOut(
                context,
                view,
                1f,
                0f,
                animated,
                runOnAnimationEnd);
    }

    public static void fadeOut(
            Context context,
            View view,
            float startAlpha,
            float endAlpha,
            boolean animated,
            Runnable runOnAnimationEnd
    ) {
        view.setAlpha(startAlpha);
        view.animate().
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
