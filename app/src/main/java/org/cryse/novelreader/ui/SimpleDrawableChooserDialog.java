package org.cryse.novelreader.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.view.View;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import org.cryse.novelreader.R;

import java.util.Collection;

/**
 * @author Aidan Follestad (afollestad)
 */
public class SimpleDrawableChooserDialog extends DialogFragment implements View.OnClickListener {

    private Callback mCallback;
    private Drawable[] mDrawables;
    private Theme mTheme = Theme.LIGHT;

    public SimpleDrawableChooserDialog() {
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() != null) {
            Integer index = (Integer) v.getTag();
            mCallback.onColorSelection(index, mDrawables[index], shiftColor(mDrawables[index]));
            dismiss();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.dialog_choose_read_bg_color)
                .theme(Theme.DARK)
                .autoDismiss(false)
                .customView(R.layout.dialog_color_chooser, false)
                .build();


        final GridLayout list = (GridLayout) dialog.getCustomView().findViewById(R.id.grid);
        final int preselect = getArguments().getInt("preselect", -1);

        for (int i = 0; i < list.getChildCount(); i++) {
            FrameLayout child = (FrameLayout) list.getChildAt(i);
            if (i < mDrawables.length) {
                child.setTag(i);
                child.setOnClickListener(this);
                child.getChildAt(0).setVisibility(preselect == i ? View.VISIBLE : View.GONE);
            } else {
                child.getChildAt(0).setVisibility(View.GONE);
                continue;
            }
            Drawable selector = createSelector(mDrawables[i]);
            setBackgroundCompat(child, selector);
        }
        return dialog;
    }

    private void setBackgroundCompat(View view, Drawable d) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            view.setBackground(d);
        else view.setBackgroundDrawable(d);
    }

    private Drawable shiftColor(Drawable drawable) {
        /*float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.9f; // value component
        return Color.HSVToColor(hsv);*/
        drawable.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.DARKEN));
        return drawable;
    }

    private Drawable createSelector(Drawable drawable) {

        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{-android.R.attr.state_pressed}, drawable);
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, drawable);
        return stateListDrawable;
    }

    /*private int shiftSelectorColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.9f; // value component
        return Color.HSVToColor(hsv);
    }*/

    /*private Drawable createSelector(int color) {
        ShapeDrawable coloredCircle = new ShapeDrawable(new OvalShape());
        coloredCircle.getPaint().setColor(color);
        ShapeDrawable darkerCircle = new ShapeDrawable(new OvalShape());
        darkerCircle.getPaint().setColor(shiftSelectorColor(color));

        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{-android.R.attr.state_pressed}, coloredCircle);
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, darkerCircle);
        return stateListDrawable;
    }*/

    public void show(AppCompatActivity context, int preselect, Callback callback) {
        Bundle args = new Bundle();
        args.putInt("preselect", preselect);
        setArguments(args);
        mCallback = callback;
        show(context.getSupportFragmentManager(), "COLOR_SELECTOR");
    }

    /*@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callback))
            throw new RuntimeException("The Activity must implement Callback to be used by ColorChooserDialog.");
        mCallback = (Callback) activity;
    }*/

    public SimpleDrawableChooserDialog setDrawables(Drawable[] drawables) {
        mDrawables = drawables;
        return this;
    }

    public SimpleDrawableChooserDialog setDrawables(Collection<Drawable> drawables) {
        mDrawables = drawables.toArray(new Drawable[drawables.size()]);
        return this;
    }

    public SimpleDrawableChooserDialog setColorDrawables(Context context, @ArrayRes int colorsResId) {
        final TypedArray ta = context.getResources().obtainTypedArray(colorsResId);
        mDrawables = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            ShapeDrawable coloredCircle = new ShapeDrawable(new OvalShape());
            coloredCircle.getPaint().setColor(ta.getColor(i, 0));
            mDrawables[i] = coloredCircle;
        }
        ta.recycle();
        return this;
    }

    public SimpleDrawableChooserDialog setTheme(Theme theme) {
        mTheme = theme;
        return this;
    }

    public interface Callback {
        void onColorSelection(int index, Drawable color, Drawable darker);
    }
}