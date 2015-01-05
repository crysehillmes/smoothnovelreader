package org.cryse.novelreader.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.util.ColorUtils;
import org.cryse.novelreader.util.RunTimeStore;

import javax.inject.Inject;

/**
 * Created by cryse on 11/11/14.
 */
public class FadeTransitionActivity extends Activity{
    private Bitmap mScreenShot;

    @Inject
    RunTimeStore mRunTimeStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SmoothReaderApplication.get(this).inject(this);
        setContentView(R.layout.dialog_fade_transition);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(ColorUtils.getColorFromAttr(this, R.attr.colorPrimaryDark));
        ImageView imageView = (ImageView)findViewById(R.id.screenshot_imageview);
        if (savedInstanceState != null) {
            mScreenShot = savedInstanceState.getParcelable("screen_shot");
        } else if(mRunTimeStore.containsKey("screen_shot")) {
            mScreenShot = (Bitmap)mRunTimeStore.get("screen_shot");
            mRunTimeStore.remove("screen_shot");
            /*mScreenShot = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("screen_shot"), 0, getIntent().getByteArrayExtra("screen_shot").length);*/
            //mScreenShot = intent.getParcelableExtra("screen_shot");
        }
        if(imageView != null && mScreenShot != null)
            imageView.setImageBitmap(mScreenShot);
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.root_container);
        ViewTreeObserver vto = frameLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver viewTreeObserver = frameLayout.getViewTreeObserver();

                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    finish();
                }, 75);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this);
                } else {
                    viewTreeObserver.removeGlobalOnLayoutListener(this);
                }
            }});
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mScreenShot != null)
            mScreenShot.recycle();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.animation_fade_out);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("screen_shot", mScreenShot);
    }
}
