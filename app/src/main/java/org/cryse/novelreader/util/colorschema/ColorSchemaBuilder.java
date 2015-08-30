package org.cryse.novelreader.util.colorschema;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ArrayRes;
import android.support.annotation.DimenRes;
import android.support.annotation.IntRange;
import android.support.annotation.StringRes;
import android.support.v4.content.res.ResourcesCompat;

import com.amulyakhare.textdrawable.TextDrawable;

import org.cryse.novelreader.R;
import org.cryse.novelreader.util.ColorUtils;
import org.cryse.novelreader.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class ColorSchemaBuilder {
    private static final int COLOR_BG_COUNT = 8;
    private Context mContext;
    private int mTextSize;
    private int mDisplayBorderSize;
    private String mDisplayText;

    private ColorSchemaBuilder(Context context) {
        mContext = context;
        mTextSize = (int) UIUtils.sp2px(mContext, 14f);
        mDisplayBorderSize = 4;
        mDisplayText = "abc";
    }

    public static ColorSchemaBuilder with(Context context) {
        return new ColorSchemaBuilder(context);
    }

    private static int getBgColorFromColorsArray(Context context, @ArrayRes int colorsResId, int index) {
        final TypedArray ta = context.getResources().obtainTypedArray(colorsResId);
        int color = ta.getColor(index, Color.WHITE);
        ta.recycle();
        return color;
    }

    private static Drawable getBgFromDrawablesArray(Context context, @ArrayRes int drawablesResId, int index) {
        final TypedArray ta = context.getResources().obtainTypedArray(drawablesResId);
        int drawableId = ta.getResourceId(index, -1);
        ta.recycle();
        return ResourcesCompat.getDrawable(context.getResources(), drawableId, null);
    }

    private static int getTextColorFromColorsArray(Context context, @ArrayRes int colorsResId, int index) {
        final TypedArray ta = context.getResources().obtainTypedArray(colorsResId);
        int color = ta.getColor(index, Color.WHITE);
        ta.recycle();
        return color;
    }

    public ColorSchemaBuilder textSize(int textSize) {
        mTextSize = textSize;
        return this;
    }

    public ColorSchemaBuilder textSizeDimen(@DimenRes int textSizeDimen) {
        mTextSize = mContext.getResources().getDimensionPixelSize(textSizeDimen);
        return this;
    }

    public ColorSchemaBuilder borderSize(int textColor) {
        mDisplayBorderSize = textColor;
        return this;
    }

    public ColorSchemaBuilder borderSizeRes(@DimenRes int borderSizeDimen) {
        mDisplayBorderSize = mContext.getResources().getDimensionPixelSize(borderSizeDimen);
        return this;
    }

    public ColorSchemaBuilder text(String text) {
        mDisplayText = text;
        return this;
    }

    public ColorSchemaBuilder textRes(@StringRes int stringRes) {
        mDisplayText = mContext.getString(stringRes);
        return this;
    }

    public ColorSchema darkMode() {
        int bgColor = ColorUtils.getColor(mContext.getResources(), R.color.theme_read_bg_color_white);
        int textColor = ColorUtils.getColor(mContext.getResources(), R.color.text_color_night_read);
        return new ColorSchema(
                textColor,
                new ColorDrawable(bgColor),
                displayDrawableFromColor(textColor, bgColor)
        );
    }

    public List<Drawable> displayDrawables() {
        List<Drawable> colorSchemas = new ArrayList<>(10);
        for (int i = 0; i < 12; i++) {
            colorSchemas.add(displayDrawableByIndex(i));
        }
        return colorSchemas;
    }

    public ColorSchema defaultSchema() {
        return fromColorByIndex(0);
    }

    public ColorSchema byIndex(int index) {
        if (index >= 0 && index < 8)
            return fromColorByIndex(index);
        else if (index >= 8 && index < 12)
            return fromDrawableByIndex(index);
        else
            return defaultSchema();
    }

    private ColorSchema fromColorByIndex(@IntRange(from = 0, to = 7) int index) {
        int bgColor = getBgColorFromColorsArray(mContext, R.array.read_background_from_colors, index);
        int textColor = getTextColorFromColorsArray(mContext, R.array.read_text_color_from_colors, index);
        return fromColor(textColor, bgColor);
    }

    private ColorSchema fromDrawableByIndex(@IntRange(from = 8, to = 11) int index) {
        Drawable drawable = getBgFromDrawablesArray(mContext, R.array.read_background_from_drawables, index - COLOR_BG_COUNT);
        int textColor = getTextColorFromColorsArray(mContext, R.array.read_text_color_from_drawables, index - COLOR_BG_COUNT);
        return fromDrawable(textColor, drawable);
    }

    public Drawable displayDrawableByIndex(int index) {
        if (index >= 0 && index < 8)
            return displayDrawableFromColorByIndex(index);
        else if (index >= 8 && index < 12)
            return displayDrawableFromDrawableByIndex(index);
        else
            return displayDrawableFromColorByIndex(0);
    }

    private Drawable displayDrawableFromColorByIndex(@IntRange(from = 0, to = 7) int index) {
        int bgColor = getBgColorFromColorsArray(mContext, R.array.read_background_from_colors, index);
        int textColor = getTextColorFromColorsArray(mContext, R.array.read_text_color_from_colors, index);
        return displayDrawableFromColor(textColor, bgColor);
    }

    private Drawable displayDrawableFromDrawableByIndex(@IntRange(from = 8, to = 11) int index) {
        Drawable bgDrawable = getBgFromDrawablesArray(mContext, R.array.read_background_from_drawables, index - COLOR_BG_COUNT);
        int textColor = getTextColorFromColorsArray(mContext, R.array.read_text_color_from_drawables, index - COLOR_BG_COUNT);
        return displayDrawableFromDrawable(textColor, bgDrawable);
    }

    private ColorSchema fromColor(int textColor, int bgColor) {
        return fromDrawable(textColor, new ColorDrawable(bgColor));
    }

    private ColorSchema fromDrawable(int textColor, Drawable bgDrawable) {
        return new ColorSchema(
                textColor,
                bgDrawable,
                displayDrawableFromDrawable(textColor, bgDrawable)
        );
    }

    private Drawable displayDrawableFromColor(int textColor, int bgColor) {
        return TextDrawable.builder()
                .beginConfig()
                .textColor(textColor)
                .fontSize(mTextSize)
                .withBorder(mDisplayBorderSize)
                .endConfig()
                .buildRect(mDisplayText, bgColor);
    }

    private Drawable displayDrawableFromDrawable(int textColor, Drawable bgDrawable) {
        Drawable textDrawable = TextDrawable.builder()
                .beginConfig()
                .textColor(textColor)
                .fontSize(mTextSize)
                .withBorder(mDisplayBorderSize)
                .endConfig()
                .buildRect(mDisplayText, Color.TRANSPARENT);
        return new LayerDrawable(new Drawable[]{bgDrawable, textDrawable});
    }
}
